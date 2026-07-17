package io.coreplatform.openapi.rate.application.service;

import io.coreplatform.openapi.rate.application.domain.RateLimitPolicy;
import io.coreplatform.openapi.rate.application.domain.RateLimitResult;
import io.coreplatform.openapi.rate.application.domain.TokenBucketState;
import io.coreplatform.openapi.rate.application.port.RateLimitPolicyRepository;
import io.coreplatform.openapi.rate.application.port.RateLimitStore;
import io.coreplatform.openapi.rate.infrastructure.config.RateLimitProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Core rate limit check service.
 * Called by GatewayFilter after authentication and authorization.
 *
 * Strategy priority: Application-level > API-level > default config
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitService {

    private final RateLimitStore store;
    private final RateLimitPolicyRepository policyRepository;
    private final RateLimitProperties properties;

    /**
     * Check if the request should be rate-limited.
     *
     * @param apiId         API being called
     * @param applicationId calling application (null if not available)
     * @param identityId    caller identity for usage tracking
     * @return rate limit decision with response header values
     */
    public RateLimitResult check(Long apiId, Long applicationId, String identityId) {
        if (!properties.isEnabled()) {
            return RateLimitResult.builder()
                    .allowed(true)
                    .reason("Rate limiting disabled")
                    .limit(0L)
                    .remaining(0L)
                    .resetSeconds(0L)
                    .build();
        }

        // 1. Try Application-level policy first (most specific)
        if (applicationId != null) {
            List<RateLimitPolicy> appPolicies =
                    policyRepository.findByScopeAndScopeId("APPLICATION", applicationId);
            for (RateLimitPolicy policy : appPolicies) {
                String key = "rate:app:" + applicationId + ":api:" + apiId;
                TokenBucketState state = store.tryAcquire(key, policy);
                if (!state.isAllowed()) {
                    log.debug("Rate limited by application policy: appId={}, apiId={}, policy={}",
                            applicationId, apiId, policy.getName());
                    return RateLimitResult.builder()
                            .allowed(false)
                            .reason("Application rate limit exceeded: " + policy.getName())
                            .limit(state.getLimit())
                            .remaining(state.getRemaining())
                            .resetSeconds(state.getResetSeconds())
                            .build();
                }
            }
        }

        // 2. Try API-level policy
        List<RateLimitPolicy> apiPolicies =
                policyRepository.findByScopeAndScopeId("API", apiId);
        for (RateLimitPolicy policy : apiPolicies) {
            String key = "rate:api:" + apiId + ":" + (applicationId != null ? applicationId : "anon");
            TokenBucketState state = store.tryAcquire(key, policy);
            if (!state.isAllowed()) {
                log.debug("Rate limited by API policy: apiId={}, policy={}",
                        apiId, policy.getName());
                return RateLimitResult.builder()
                        .allowed(false)
                        .reason("API rate limit exceeded: " + policy.getName())
                        .limit(state.getLimit())
                        .remaining(state.getRemaining())
                        .resetSeconds(state.getResetSeconds())
                        .build();
            }
            return RateLimitResult.builder()
                    .allowed(true)
                    .limit(state.getLimit())
                    .remaining(state.getRemaining())
                    .resetSeconds(state.getResetSeconds())
                    .build();
        }

        // 3. Fallback to default policy from config
        RateLimitPolicy defaultPolicy = buildDefaultPolicy();
        String key = "rate:default:" + identityId + ":api:" + apiId;
        TokenBucketState state = store.tryAcquire(key, defaultPolicy);

        return RateLimitResult.builder()
                .allowed(state.isAllowed())
                .reason(state.isAllowed() ? null : "Default rate limit exceeded")
                .limit(state.getLimit())
                .remaining(state.getRemaining())
                .resetSeconds(state.getResetSeconds())
                .build();
    }

    private RateLimitPolicy buildDefaultPolicy() {
        var cfg = properties.getDefaultPolicy();
        return RateLimitPolicy.builder()
                .id(0L)
                .name("default")
                .scope("DEFAULT")
                .algorithm("TOKEN_BUCKET")
                .limitValue(cfg.getLimitValue())
                .refillRate(cfg.getRefillRate())
                .refillPeriod(cfg.getRefillPeriod())
                .status("ACTIVE")
                .build();
    }
}