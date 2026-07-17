package io.coreplatform.openapi.application.port;

import io.coreplatform.openapi.application.domain.ApiResponse;

import java.util.List;
import java.util.Optional;

public interface ResponseRepository {

    ApiResponse save(ApiResponse response);

    Optional<ApiResponse> findById(Long id);

    List<ApiResponse> findByApiId(Long apiId);

    void deleteById(Long id);

    void deleteByApiId(Long apiId);
}
