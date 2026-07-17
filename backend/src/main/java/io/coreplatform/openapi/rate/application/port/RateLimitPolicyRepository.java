package io.coreplatform.openapi.rate.application.port;

import io.coreplatform.openapi.rate.application.domain.RateLimitPolicy;

import java.util.List;
import java.util.Optional;

public interface RateLimitPolicyRepository {

    Optional<RateLimitPolicy> findById(Long id);

    List<RateLimitPolicy> findByScopeAndScopeId(String scope, Long scopeId);

    List<RateLimitPolicy> findAll();

    List<RateLimitPolicy> findByStatus(String status);

    RateLimitPolicy save(RateLimitPolicy policy);

    void deleteById(Long id);
}