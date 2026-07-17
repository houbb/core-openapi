package io.coreplatform.openapi.application.port;

import io.coreplatform.openapi.application.domain.ApiExample;

import java.util.List;
import java.util.Optional;

public interface ExampleRepository {

    ApiExample save(ApiExample example);

    Optional<ApiExample> findById(Long id);

    List<ApiExample> findByApiId(Long apiId);

    void deleteById(Long id);

    void deleteByApiId(Long apiId);
}
