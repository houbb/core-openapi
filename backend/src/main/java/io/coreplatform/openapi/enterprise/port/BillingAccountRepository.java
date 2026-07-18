package io.coreplatform.openapi.enterprise.port;

import io.coreplatform.openapi.enterprise.domain.BillingAccount;

import java.util.Optional;

public interface BillingAccountRepository {
    BillingAccount save(BillingAccount account);
    Optional<BillingAccount> findById(Long id);
    Optional<BillingAccount> findByOrganizationId(Long organizationId);
    void deleteById(Long id);
}
