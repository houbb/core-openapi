package io.coreplatform.openapi.rate.application.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RateLimitResult {
    /** Whether the request is allowed */
    private boolean allowed;
    /** Reason for rejection (when not allowed) */
    private String reason;
    /** X-RateLimit-Limit header value */
    private Long limit;
    /** X-RateLimit-Remaining header value */
    private Long remaining;
    /** X-RateLimit-Reset header value (seconds until reset) */
    private Long resetSeconds;
}