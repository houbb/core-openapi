package io.coreplatform.openapi.application.port;

import io.coreplatform.openapi.application.domain.ApiVersion;

import java.util.List;
import java.util.Optional;

public interface VersionRepository {

    ApiVersion save(ApiVersion version);

    Optional<ApiVersion> findById(Long id);

    List<ApiVersion> findByApiId(Long apiId);

    void deleteById(Long id);
}
