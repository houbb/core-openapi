package io.coreplatform.openapi.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.coreplatform.openapi.application.domain.AccessLog;
import io.coreplatform.openapi.application.port.AccessLogRepository;
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
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayFilter extends OncePerRequestFilter {

    private final RequestRouter requestRouter;
    private final RequestProxy requestProxy;
    private final RequestValidator requestValidator;
    private final AccessLogRepository accessLogRepository;
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
        RequestContext ctx = null;
        RequestRouter.RouteResult matchedRoute = null;
        int responseStatus = 200;
        String errorMessage = null;

        try {
            // 1. Create request context
            ctx = RequestContext.builder()
                    .requestId(requestId)
                    .traceId(requestId)
                    .clientId(getClientId(request))
                    .userId(null) // Deferred to auth phase
                    .tenantId(null) // Deferred to auth phase
                    .timestamp(LocalDateTime.now())
                    .build();
            RequestContextHolder.init(ctx);
            log.debug("Gateway request: id={}, path={}, method={}", requestId, backendPath, httpMethod);

            // 2. Match route
            matchedRoute = requestRouter.resolve(backendPath, httpMethod);

            // 2.5 Validate request against stored schemas
            if (matchedRoute.route().getApiId() != null) {
                java.util.List<String> validationErrors = requestValidator.validate(matchedRoute.route().getApiId(), request);
                if (!validationErrors.isEmpty()) {
                    String detail = String.join("; ", validationErrors);
                    throw new GatewayException(GatewayErrorCode.INVALID_REQUEST,
                            "请求参数校验失败: " + detail);
                }
            }

            // 3. Forward to backend
            RequestProxy.ProxyResult proxyResult = requestProxy.forward(
                    matchedRoute.fullTargetUrl(),
                    httpMethod,
                    request,
                    matchedRoute.route().getTimeout() != null ? matchedRoute.route().getTimeout() : 30000
            );

            responseStatus = proxyResult.statusCode();

            // 4. Build unified response
            Object responseData = parseResponseBody(proxyResult.body());
            UnifiedResponse<Object> unifiedResponse = UnifiedResponse.success(responseData, requestId);

            // 5. Write response
            writeJsonResponse(response, proxyResult.isSuccess() ? HttpServletResponse.SC_OK : responseStatus, unifiedResponse);

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
            // 6. Write access log (sync for MVP; can be async later)
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

            // Cleanup
            RequestContextHolder.clear();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith(GATEWAY_PREFIX);
    }

    private String getClientId(HttpServletRequest request) {
        // Extract from X-Client-Id header or use a default
        String clientId = request.getHeader("X-Client-Id");
        return clientId != null ? clientId : "anonymous";
    }

    private Object parseResponseBody(byte[] body) {
        if (body == null || body.length == 0) {
            return null;
        }
        try {
            // Try to parse as JSON, fallback to string
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
            case SERVICE_UNAVAILABLE, SERVICE_TIMEOUT, BACKEND_ERROR -> HttpServletResponse.SC_BAD_GATEWAY;
            case GATEWAY_INTERNAL_ERROR -> HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            default -> HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        };
    }
}