package io.coreplatform.openapi.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.coreplatform.openapi.application.domain.AccessLog;
import io.coreplatform.openapi.application.domain.ApiKeyUsageLog;
import io.coreplatform.openapi.application.port.AccessLogRepository;
import io.coreplatform.openapi.application.port.ApiKeyUsageLogRepository;
import io.coreplatform.openapi.security.domain.ApiSecurityPolicy;
import io.coreplatform.openapi.security.domain.AuthenticationToken;
import io.coreplatform.openapi.security.domain.SecurityPolicyDecision;
import io.coreplatform.openapi.security.port.ApiSecurityPolicyRepository;
import io.coreplatform.openapi.security.service.AuthenticationService;
import io.coreplatform.openapi.security.service.AuthorizationService;
import io.coreplatform.openapi.security.service.SecurityAuditService;
import io.coreplatform.openapi.security.service.SecurityPolicyService;
import io.coreplatform.openapi.rate.application.domain.RateLimitResult;
import io.coreplatform.openapi.rate.application.service.RateLimitService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayFilter extends OncePerRequestFilter {

    private final RequestRouter requestRouter;
    private final RequestProxy requestProxy;
    private final RequestValidator requestValidator;
    private final AuthenticationService authenticationService;
    private final AuthorizationService authorizationService;
    private final SecurityPolicyService securityPolicyService;
    private final SecurityAuditService securityAuditService;
    private final RateLimitService rateLimitService;
    private final ApiSecurityPolicyRepository apiSecurityPolicyRepository;
    private final AccessLogRepository accessLogRepository;
    private final ApiKeyUsageLogRepository apiKeyUsageLogRepository;
    private final ObjectMapper objectMapper;

    private static final String GATEWAY_PREFIX = "/gateway";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // Only intercept /gateway/** requests
        if (!requestPath.startsWith(GATEWAY_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Strip gateway prefix to get the actual backend path
        String backendPath = requestPath.substring(GATEWAY_PREFIX.length());
        if (backendPath.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();
        String httpMethod = request.getMethod();
        String clientIp = getClientIp(request);
        RequestContext ctx = null;
        RequestRouter.RouteResult matchedRoute = null;
        AuthenticationToken authToken = null;
        int responseStatus = 200;
        String errorMessage = null;

        try {
            // 1. Create request context
            ctx = RequestContext.builder()
                    .requestId(requestId)
                    .traceId(requestId)
                    .clientId(getClientId(request))
                    .userId(null)
                    .tenantId(null)
                    .authType(null)
                    .permissions(null)
                    .timestamp(LocalDateTime.now())
                    .build();
            RequestContextHolder.init(ctx);
            log.debug("Gateway request: id={}, path={}, method={}", requestId, backendPath, httpMethod);

            // 2. Match route
            matchedRoute = requestRouter.resolve(backendPath, httpMethod);

            // 3. Authenticate — API Key or JWT
            try {
                authToken = authenticationService.authenticate(request, matchedRoute);
                ctx.setClientId(authToken.getPrincipal());
                ctx.setUserId(authToken.getUserId() != null ? authToken.getUserId().toString() : null);
                ctx.setTenantId(authToken.getTenantId());
                ctx.setAuthType(authToken.getTokenType());
                ctx.setPermissions(authToken.getPermissions());
                log.debug("Auth success: type={}, principal={}, tenant={}",
                        authToken.getTokenType(), authToken.getPrincipal(), authToken.getTenantId());

                // Audit: auth success
                securityAuditService.recordAuthSuccess(
                        authToken.getTokenType(), authToken.getPrincipal(),
                        clientIp, requestId, authToken.getTenantId());

            } catch (GatewayException e) {
                // Audit: auth failure
                securityAuditService.recordAuthFailure(
                        "UNKNOWN", "unknown",
                        e.getMessage(), clientIp, requestId, "");
                throw e;
            }

            // 4. Load security policy for this API
            ApiSecurityPolicy policy = null;
            if (matchedRoute != null && matchedRoute.route().getApiId() != null) {
                Optional<ApiSecurityPolicy> policyOpt =
                        apiSecurityPolicyRepository.findByApiId(matchedRoute.route().getApiId());
                policy = policyOpt.orElse(null);
            }

            // 5. Evaluate security policy (IP, time, tenant checks)
            if (policy != null) {
                SecurityPolicyDecision decision = securityPolicyService.evaluate(policy, request);
                if (!decision.isAllowed()) {
                    securityAuditService.recordPolicyDenied(
                            authToken.getTokenType(), authToken.getPrincipal(),
                            decision.getReason(), clientIp, requestId, authToken.getTenantId());
                    throw new GatewayException(GatewayErrorCode.PERMISSION_DENIED, decision.getReason());
                }

                // 6. Authorize — RBAC permission check
                if (decision.getRequiredPermission() != null && !decision.getRequiredPermission().isBlank()) {
                    boolean authorized = authorizationService.authorize(authToken, decision.getRequiredPermission());
                    if (!authorized) {
                        securityAuditService.recordPermissionDenied(
                                authToken.getTokenType(), authToken.getPrincipal(),
                                decision.getRequiredPermission(), clientIp, requestId, authToken.getTenantId());
                        throw new GatewayException(GatewayErrorCode.PERMISSION_DENIED,
                                "缺少权限: " + decision.getRequiredPermission());
                    }
                }
            }

            // 7. Rate limit check — Token Bucket
            if (matchedRoute != null && matchedRoute.route().getApiId() != null) {
                Long appId = extractApplicationId(authToken);
                RateLimitResult rateLimitResult = rateLimitService.check(
                        matchedRoute.route().getApiId(), appId, authToken.getPrincipal());

                // Set rate limit response headers
                response.setHeader("X-RateLimit-Limit", String.valueOf(rateLimitResult.getLimit()));
                response.setHeader("X-RateLimit-Remaining", String.valueOf(rateLimitResult.getRemaining()));
                response.setHeader("X-RateLimit-Reset", String.valueOf(rateLimitResult.getResetSeconds()));

                if (!rateLimitResult.isAllowed()) {
                    securityAuditService.recordRiskAlert(
                            authToken.getPrincipal(), rateLimitResult.getReason(),
                            clientIp, requestId, authToken.getTenantId());
                    throw new GatewayException(GatewayErrorCode.RATE_LIMIT_EXCEEDED, rateLimitResult.getReason());
                }
            }

            // 8. Validate request against stored schemas
            if (matchedRoute != null && matchedRoute.route().getApiId() != null) {
                java.util.List<String> validationErrors = requestValidator.validate(
                        matchedRoute.route().getApiId(), request);
                if (!validationErrors.isEmpty()) {
                    String detail = String.join("; ", validationErrors);
                    throw new GatewayException(GatewayErrorCode.INVALID_REQUEST,
                            "请求参数校验失败: " + detail);
                }
            }

            // 9. Forward to backend
            RequestProxy.ProxyResult proxyResult = requestProxy.forward(
                    matchedRoute.fullTargetUrl(),
                    httpMethod,
                    request,
                    matchedRoute.route().getTimeout() != null ? matchedRoute.route().getTimeout() : 30000
            );

            responseStatus = proxyResult.statusCode();

            // 10. Build unified response
            Object responseData = parseResponseBody(proxyResult.body());
            UnifiedResponse<Object> unifiedResponse = UnifiedResponse.success(responseData, requestId);

            // 11. Write response
            writeJsonResponse(response,
                    proxyResult.isSuccess() ? HttpServletResponse.SC_OK : responseStatus,
                    unifiedResponse);

        } catch (GatewayException e) {
            responseStatus = mapToHttpStatus(e.getGatewayErrorCode());
            errorMessage = e.getMessage();
            log.warn("Gateway error: code={}, message={}, requestId={}",
                    e.getGatewayErrorCode().getCode(), e.getMessage(), requestId);

            UnifiedResponse<Object> errorResponse = UnifiedResponse.error(
                    e.getGatewayErrorCode().getCode(),
                    e.getGatewayErrorCode().getMessage(),
                    requestId
            );
            writeJsonResponse(response, responseStatus, errorResponse);

        } catch (Exception e) {
            responseStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            errorMessage = e.getMessage();
            log.error("Unexpected gateway error: requestId={}", requestId, e);

            UnifiedResponse<Object> errorResponse = UnifiedResponse.error(
                    GatewayErrorCode.GATEWAY_INTERNAL_ERROR.getCode(),
                    GatewayErrorCode.GATEWAY_INTERNAL_ERROR.getMessage(),
                    requestId
            );
            writeJsonResponse(response, responseStatus, errorResponse);

        } finally {
            // 12. Write access log (sync for MVP; can be async later)
            long costTime = System.currentTimeMillis() - startTime;
            try {
                AccessLog accessLog = AccessLog.builder()
                        .requestId(requestId)
                        .apiId(matchedRoute != null ? matchedRoute.route().getApiId() : null)
                        .clientId(ctx != null ? ctx.getClientId() : "")
                        .requestMethod(httpMethod)
                        .requestPath(backendPath)
                        .targetUrl(matchedRoute != null ? matchedRoute.fullTargetUrl() : "")
                        .requestTime(LocalDateTime.now().minusNanos(costTime * 1_000_000))
                        .responseTime(LocalDateTime.now())
                        .statusCode(responseStatus)
                        .costTime(costTime)
                        .errorMessage(errorMessage)
                        .createTime(LocalDateTime.now())
                        .build();
                accessLogRepository.save(accessLog);
            } catch (Exception logEx) {
                log.error("Failed to write access log: requestId={}", requestId, logEx);
            }

            // 13. Write API key usage log
            try {
                if (ctx != null && ctx.getClientId() != null && matchedRoute != null) {
                    writeUsageLogForSuccess(ctx, matchedRoute, requestId);
                }
            } catch (Exception logEx) {
                log.error("Failed to write API key usage log: requestId={}", requestId, logEx);
            }

            // Cleanup
            RequestContextHolder.clear();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith(GATEWAY_PREFIX);
    }

    private String getClientId(HttpServletRequest request) {
        String clientId = request.getHeader("X-Client-Id");
        return clientId != null ? clientId : "anonymous";
    }

    private Object parseResponseBody(byte[] body) {
        if (body == null || body.length == 0) {
            return null;
        }
        try {
            return objectMapper.readTree(body);
        } catch (Exception e) {
            return new String(body);
        }
    }

    private void writeJsonResponse(HttpServletResponse response, int status, Object body) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), body);
    }

    private int mapToHttpStatus(GatewayErrorCode errorCode) {
        return switch (errorCode) {
            case ROUTE_NOT_FOUND -> HttpServletResponse.SC_NOT_FOUND;
            case INVALID_REQUEST -> HttpServletResponse.SC_BAD_REQUEST;
            case INVALID_API_KEY, API_KEY_EXPIRED, API_KEY_DISABLED,
                 INVALID_JWT, JWT_EXPIRED -> HttpServletResponse.SC_UNAUTHORIZED;
            case PERMISSION_DENIED, TENANT_REQUIRED, IP_NOT_ALLOWED,
                 TIME_NOT_ALLOWED -> HttpServletResponse.SC_FORBIDDEN;
            case RATE_LIMIT_EXCEEDED -> 429; // Too Many Requests
            case RISK_BLOCKED -> 429; // Too Many Requests
            case SERVICE_UNAVAILABLE, SERVICE_TIMEOUT, BACKEND_ERROR ->
                    HttpServletResponse.SC_BAD_GATEWAY;
            case GATEWAY_INTERNAL_ERROR -> HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            default -> HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        };
    }

    private void writeUsageLog(Long apiKeyId, Long apiId, String requestId, String ip, String status) {
        try {
            ApiKeyUsageLog log = ApiKeyUsageLog.builder()
                    .apiKeyId(apiKeyId)
                    .apiId(apiId)
                    .requestId(requestId)
                    .ip(ip != null ? ip : "")
                    .timestamp(LocalDateTime.now())
                    .status(status)
                    .build();
            apiKeyUsageLogRepository.save(log);
        } catch (Exception e) {
            // Swallow — usage log failure should never break the gateway
        }
    }

    private void writeUsageLogForSuccess(RequestContext ctx, RequestRouter.RouteResult matchedRoute, String requestId) {
        try {
            ApiKeyUsageLog log = ApiKeyUsageLog.builder()
                    .apiKeyId(null)
                    .apiId(matchedRoute != null ? matchedRoute.route().getApiId() : null)
                    .requestId(requestId)
                    .ip("")
                    .timestamp(LocalDateTime.now())
                    .status("SUCCESS")
                    .build();
            apiKeyUsageLogRepository.save(log);
        } catch (Exception e) {
            // Swallow
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Extract application ID from the authentication token.
     * For API Key auth, the applicationId is available; for JWT it may not be.
     */
    private Long extractApplicationId(AuthenticationToken authToken) {
        if (authToken == null) return null;
        return authToken.getApplicationId();
    }
}