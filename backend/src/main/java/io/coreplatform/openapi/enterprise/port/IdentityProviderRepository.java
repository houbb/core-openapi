package io.coreplatform.openapi.enterprise.port;

import io.coreplatform.openapi.enterprise.domain.IdentityProvider;

import java.util.List;
import java.util.Optional;

public interface IdentityProviderRepository {
    IdentityProvider save(IdentityProvider provider);
    Optional<IdentityProvider> findById(Long id);
    List<IdentityProvider> findByOrganizationId(Long organizationId);
    Optional<IdentityProvider> findDefaultByOrgId(Long organizationId);
    void deleteById(Long id);
}
