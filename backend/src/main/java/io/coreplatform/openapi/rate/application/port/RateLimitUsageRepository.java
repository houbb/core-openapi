package io.coreplatform.openapi.rate.application.port;

import io.coreplatform.openapi.rate.application.domain.RateLimitUsage;

import java.util.List;

public interface RateLimitUsageRepository {

    void save(RateLimitUsage usage);

    List<RateLimitUsage> findByPolicyId(Long policyId);

    List<RateLimitUsage> findByIdentityId(String identityId);
}