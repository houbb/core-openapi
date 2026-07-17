package io.coreplatform.openapi.rate.infrastructure.store;

import io.coreplatform.openapi.rate.application.domain.RateLimitPolicy;
import io.coreplatform.openapi.rate.application.domain.TokenBucketState;
import io.coreplatform.openapi.rate.application.port.RateLimitStore;
import io.coreplatform.openapi.rate.infrastructure.config.RateLimitProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * In-memory Token Bucket implementation using ConcurrentHashMap.
 * Each bucket tracks current token count and last refill timestamp.
 */
@Slf4j
@Component
public class MemoryRateLimitStore implements RateLimitStore {

    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleanupExecutor;
    private final RateLimitProperties properties;

    public MemoryRateLimitStore(RateLimitProperties properties) {
        this.properties = properties;
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "rate-limit-cleanup");
            t.setDaemon(true);
            return t;
        });

        long interval = properties.getCleanupIntervalSeconds();
        long ttl = properties.getBucketTtlSeconds();
        this.cleanupExecutor.scheduleAtFixedRate(
                () -> cleanupStaleBuckets(ttl),
                interval, interval, TimeUnit.SECONDS);
    }

    @Override
    public TokenBucketState tryAcquire(String key, RateLimitPolicy policy) {
        TokenBucket bucket = buckets.computeIfAbsent(key, k -> new TokenBucket(
                policy.getLimitValue().doubleValue(),
                System.currentTimeMillis()));

        synchronized (bucket) {
            refill(bucket, policy);
            boolean allowed = bucket.tokens >= 1.0;
            if (allowed) {
                bucket.tokens -= 1.0;
            }

            long resetSeconds = computeResetSeconds(bucket, policy);

            return TokenBucketState.builder()
                    .allowed(allowed)
                    .remaining((long) Math.floor(bucket.tokens))
                    .limit(policy.getLimitValue().longValue())
                    .resetSeconds(resetSeconds)
                    .build();
        }
    }

    @Override
    public TokenBucketState getState(String key, RateLimitPolicy policy) {
        TokenBucket bucket = buckets.get(key);
        if (bucket == null) {
            return TokenBucketState.builder()
                    .allowed(true)
                    .remaining(policy.getLimitValue().longValue())
                    .limit(policy.getLimitValue().longValue())
                    .resetSeconds(0)
                    .build();
        }

        synchronized (bucket) {
            refill(bucket, policy);
            return TokenBucketState.builder()
                    .allowed(bucket.tokens >= 1.0)
                    .remaining((long) Math.floor(bucket.tokens))
                    .limit(policy.getLimitValue().longValue())
                    .resetSeconds(computeResetSeconds(bucket, policy))
                    .build();
        }
    }

    @Override
    public void reset(String key) {
        buckets.remove(key);
        log.debug("Rate limit bucket reset: key={}", key);
    }

    /**
     * Refill tokens based on elapsed time since last refill.
     */
    private void refill(TokenBucket bucket, RateLimitPolicy policy) {
        long now = System.currentTimeMillis();
        long elapsedMs = now - bucket.lastRefillTime;
        if (elapsedMs <= 0) return;

        double refillPeriodMs = policy.getRefillPeriod() * 1000.0;
        double tokensToAdd = (elapsedMs / refillPeriodMs) * policy.getRefillRate();

        if (tokensToAdd > 0) {
            bucket.tokens = Math.min(policy.getLimitValue(), bucket.tokens + tokensToAdd);
            bucket.lastRefillTime = now;
        }
    }

    /**
     * Estimate seconds until the bucket refills enough for 1 request.
     */
    private long computeResetSeconds(TokenBucket bucket, RateLimitPolicy policy) {
        double tokensNeeded = 1.0 - bucket.tokens;
        if (tokensNeeded <= 0) return 0;
        double refillPerSecond = (double) policy.getRefillRate() / policy.getRefillPeriod();
        if (refillPerSecond <= 0) return 60;
        return (long) Math.ceil(tokensNeeded / refillPerSecond);
    }

    /**
     * Remove buckets that haven't been accessed within ttlSeconds.
     */
    private void cleanupStaleBuckets(long ttlSeconds) {
        long now = System.currentTimeMillis();
        long cutoff = now - (ttlSeconds * 1000);
        int removed = 0;
        for (var entry : buckets.entrySet()) {
            TokenBucket bucket = entry.getValue();
            synchronized (bucket) {
                if (bucket.lastRefillTime < cutoff) {
                    buckets.remove(entry.getKey());
                    removed++;
                }
            }
        }
        if (removed > 0) {
            log.debug("Rate limit cleanup: removed {} stale buckets", removed);
        }
    }

    /**
     * Internal token bucket state.
     */
    static class TokenBucket {
        double tokens;
        long lastRefillTime;

        TokenBucket(double tokens, long lastRefillTime) {
            this.tokens = tokens;
            this.lastRefillTime = lastRefillTime;
        }
    }
}