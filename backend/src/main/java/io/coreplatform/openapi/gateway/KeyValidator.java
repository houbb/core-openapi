package io.coreplatform.openapi.gateway;

import io.coreplatform.openapi.application.domain.ApiKey;
import io.coreplatform.openapi.application.domain.Application;
import io.coreplatform.openapi.application.port.ApiKeyRepository;
import io.coreplatform.openapi.application.port.ApplicationRepository;
import io.coreplatform.openapi.infrastructure.crypto.KeyGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeyValidator {

    private final ApiKeyRepository apiKeyRepository;
    private final ApplicationRepository applicationRepository;
    private final KeyGenerator keyGenerator;

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Validate an API key from the request and return the authenticated context.
     *
     * @param request the HTTP servlet request
     * @param matchedRoute the matched route (may be null if no route matched yet)
     * @return the validated result with api key, application, and permissions
     * @throws GatewayException if authentication fails
     */
    public AuthResult validate(HttpServletRequest request, RequestRouter.RouteResult matchedRoute) {
        // 1. Extract key from Authorization header
        String authHeader = request.getHeader(AUTH_HEADER);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("Missing or invalid Authorization header");
            throw new GatewayException(GatewayErrorCode.INVALID_API_KEY, "缺少Authorization头");
        }

        String rawKey = authHeader.substring(BEARER_PREFIX.length()).trim();
        if (rawKey.isEmpty()) {
            throw new GatewayException(GatewayErrorCode.INVALID_API_KEY, "API Key为空");
        }

        // 2. Basic format validation
        if (!rawKey.startsWith("sk_live_") && !rawKey.startsWith("sk_test_")) {
            throw new GatewayException(GatewayErrorCode.INVALID_API_KEY, "API Key格式无效");
        }

        // 3. Hash and look up
        String keyHash = keyGenerator.hash(rawKey);
        ApiKey apiKey = apiKeyRepository.findByKeyHash(keyHash)
                .orElseThrow(() -> {
                    log.warn("API Key not found for hash");
                    return new GatewayException(GatewayErrorCode.INVALID_API_KEY, "API Key不存在");
                });

        // 4. Validate key status
        if ("DISABLED".equals(apiKey.getStatus())) {
            throw new GatewayException(GatewayErrorCode.API_KEY_DISABLED);
        }
        if ("REVOKED".equals(apiKey.getStatus())) {
            throw new GatewayException(GatewayErrorCode.INVALID_API_KEY, "API Key已吊销");
        }
        if ("EXPIRED".equals(apiKey.getStatus()) ||
                (apiKey.getExpireTime() != null && apiKey.getExpireTime().isBefore(LocalDateTime.now()))) {
            throw new GatewayException(GatewayErrorCode.API_KEY_EXPIRED);
        }
        if (!"ACTIVE".equals(apiKey.getStatus())) {
            throw new GatewayException(GatewayErrorCode.INVALID_API_KEY);
        }

        // 5. Validate parent application
        Application application = applicationRepository.findById(apiKey.getApplicationId())
                .orElseThrow(() -> new GatewayException(GatewayErrorCode.INVALID_API_KEY, "所属应用不存在"));

        if (!"ACTIVE".equals(application.getStatus())) {
            throw new GatewayException(GatewayErrorCode.INVALID_API_KEY, "应用已被禁用");
        }

        // 6. Update last used time (fire-and-forget, best-effort)
        try {
            apiKey.setLastUsedTime(LocalDateTime.now());
            apiKeyRepository.save(apiKey);
        } catch (Exception e) {
            log.debug("Failed to update last_used_time for key {}: {}", apiKey.getId(), e.getMessage());
        }

        return new AuthResult(apiKey, application);
    }

    /**
     * Result of successful API key validation.
     */
    public record AuthResult(ApiKey apiKey, Application application) {
    }
}