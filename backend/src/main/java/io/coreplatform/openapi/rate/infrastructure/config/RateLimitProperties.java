package io.coreplatform.openapi.rate.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "core-openapi.rate-limit")
public class RateLimitProperties {

    /** Master switch for rate limiting */
    private boolean enabled = true;

    /** Default policy used when no specific policy is configured */
    private DefaultPolicyConfig defaultPolicy = new DefaultPolicyConfig();

    /** Interval in seconds for cleaning up stale buckets */
    private int cleanupIntervalSeconds = 60;

    /** TTL in seconds before an unused bucket is removed */
    private int bucketTtlSeconds = 300;

    @Data
    public static class DefaultPolicyConfig {
        /** Maximum tokens in the bucket */
        private int limitValue = 100;
        /** Tokens to add each refill period */
        private int refillRate = 10;
        /** Refill period in seconds */
        private int refillPeriod = 1;
    }
}