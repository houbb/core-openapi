package io.coreplatform.openapi.application.port;

import io.coreplatform.openapi.application.domain.ApiKey;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository {

    ApiKey save(ApiKey apiKey);

    Optional<ApiKey> findById(Long id);

    Optional<ApiKey> findByKeyHash(String keyHash);

    List<ApiKey> findByApplicationId(Long applicationId);

    void deleteById(Long id);

    Long countByStatus(String status);
}
