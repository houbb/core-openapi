package io.coreplatform.openapi.rate.application.port;

import io.coreplatform.openapi.rate.application.domain.RateLimitPolicy;
import io.coreplatform.openapi.rate.application.domain.TokenBucketState;

/**
 * Abstraction for rate limit token storage.
 * MVP uses in-memory storage; future can swap to Redis.
 */
public interface RateLimitStore {

    /**
     * Try to acquire one token for the given key and policy.
     *
     * @param key    unique identifier (e.g. "rate:api:123" or "rate:app:456")
     * @param policy the rate limit policy with capacity and refill settings
     * @return current bucket state after the attempt
     */
    TokenBucketState tryAcquire(String key, RateLimitPolicy policy);

    /**
     * Get current bucket state without consuming a token.
     *
     * @param key    unique identifier
     * @param policy the rate limit policy
     * @return current bucket state
     */
    TokenBucketState getState(String key, RateLimitPolicy policy);

    /**
     * Remove all tracking data for the given key.
     */
    void reset(String key);
}
