package io.coreplatform.openapi.security.service;

import io.coreplatform.openapi.security.config.SecurityRuntimeProperties;
import io.coreplatform.openapi.security.domain.RiskEvent;
import io.coreplatform.openapi.security.port.RiskEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
@Component
public class RiskControlService {

    private final SecurityRuntimeProperties properties;
    private final RiskEventRepository riskEventRepository;
    private final ConcurrentHashMap<String, Deque<Long>> requestWindows = new ConcurrentHashMap<>();

    public RiskControlService(SecurityRuntimeProperties properties,
                               RiskEventRepository riskEventRepository) {
        this.properties = properties;
        this.riskEventRepository = riskEventRepository;
    }

    /**
     * Check if the current request exceeds the rate limit.
     *
     * @param identityId the identity identifier (appCode or JWT subject)
     * @param ip         the client IP
     * @param tenantId   the tenant ID
     * @throws RateLimitExceededException if rate limit is exceeded
     */
    public void check(String identityId, String ip, String tenantId) {
        if (!properties.getRiskControl().getRateLimit().isEnabled()) {
            return;
        }

        int maxRequests = properties.getRiskControl().getRateLimit().getMaxRequests();
        int windowSeconds = properties.getRiskControl().getRateLimit().getWindowSeconds();

        long now = System.currentTimeMillis();
        long windowStart = now - (windowSeconds * 1000L);

        Deque<Long> timestamps = requestWindows.computeIfAbsent(identityId,
                k -> new ConcurrentLinkedDeque<>());

        // Clean up old entries
        while (!timestamps.isEmpty() && timestamps.peekFirst() < windowStart) {
            timestamps.pollFirst();
        }

        // Add current timestamp
        timestamps.addLast(now);

        if (timestamps.size() > maxRequests) {
            long requestCount = timestamps.size();
            log.warn("Risk control triggered: identity={}, requests={} in {}s, threshold={}",
                    identityId, requestCount, windowSeconds, maxRequests);

            // Record risk event
            try {
                RiskEvent event = RiskEvent.builder()
                        .identityId(identityId)
                        .riskType("RATE_ANOMALY")
                        .severity("WARNING")
                        .detail("Rate limit exceeded: " + requestCount + " requests in " + windowSeconds + "s")
                        .requestCount(requestCount)
                        .windowSeconds(windowSeconds)
                        .thresholdCount((long) maxRequests)
                        .tenantId(tenantId)
                        .createdTime(LocalDateTime.now())
                        .build();
                riskEventRepository.save(event);
            } catch (Exception e) {
                log.error("Failed to record risk event", e);
            }

            throw new RateLimitExceededException(
                    "Rate limit exceeded: " + maxRequests + " requests per " + windowSeconds + "s");
        }
    }

    /**
     * Clean up the sliding window data for an identity (e.g., on key revocation).
     */
    public void reset(String identityId) {
        requestWindows.remove(identityId);
    }

    /**
     * Exception thrown when rate limit is exceeded.
     */
    public static class RateLimitExceededException extends RuntimeException {
        public RateLimitExceededException(String message) {
            super(message);
        }
    }
}