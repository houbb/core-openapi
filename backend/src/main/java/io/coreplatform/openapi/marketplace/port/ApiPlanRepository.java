package io.coreplatform.openapi.marketplace.port;

import io.coreplatform.openapi.marketplace.domain.ApiPlan;

import java.util.List;
import java.util.Optional;

public interface ApiPlanRepository {
    ApiPlan save(ApiPlan plan);
    Optional<ApiPlan> findById(Long id);
    List<ApiPlan> findByProductId(Long productId);
    void deleteById(Long id);
    Long countByProductId(Long productId);
}
