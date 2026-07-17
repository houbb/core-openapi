package io.coreplatform.openapi.application.port;

import io.coreplatform.openapi.application.domain.RequestSchema;

import java.util.Optional;

public interface RequestSchemaRepository {

    RequestSchema save(RequestSchema requestSchema);

    Optional<RequestSchema> findById(Long id);

    Optional<RequestSchema> findByApiId(Long apiId);

    void deleteById(Long id);

    void deleteByApiId(Long apiId);
}
