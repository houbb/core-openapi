package io.coreplatform.openapi.rate.application.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenBucketState {
    /** Whether the request is allowed */
    private boolean allowed;
    /** Remaining tokens in the bucket */
    private long remaining;
    /** Total bucket capacity */
    private long limit;
    /** Seconds until next token refill brings bucket to full */
    private long resetSeconds;
}