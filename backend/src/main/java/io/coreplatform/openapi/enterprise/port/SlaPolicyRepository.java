package io.coreplatform.openapi.enterprise.port;

import io.coreplatform.openapi.enterprise.domain.SlaPolicy;

import java.util.Optional;

public interface SlaPolicyRepository {
    SlaPolicy save(SlaPolicy policy);
    Optional<SlaPolicy> findById(Long id);
    Optional<SlaPolicy> findByOrganizationId(Long organizationId);
    void deleteById(Long id);
}
