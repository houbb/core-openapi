package io.coreplatform.openapi.enterprise.port;

import io.coreplatform.openapi.enterprise.domain.CompliancePolicy;

import java.util.List;
import java.util.Optional;

public interface CompliancePolicyRepository {
    CompliancePolicy save(CompliancePolicy policy);
    Optional<CompliancePolicy> findById(Long id);
    List<CompliancePolicy> findByOrganizationId(Long organizationId);
    List<CompliancePolicy> findByOrganizationIdAndType(Long organizationId, String policyType);
    void deleteById(Long id);
}
