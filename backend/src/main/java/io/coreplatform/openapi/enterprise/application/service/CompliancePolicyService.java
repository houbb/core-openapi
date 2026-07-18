package io.coreplatform.openapi.enterprise.application.service;

import io.coreplatform.openapi.enterprise.domain.CompliancePolicy;
import io.coreplatform.openapi.enterprise.port.CompliancePolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompliancePolicyService {

    private final CompliancePolicyRepository compliancePolicyRepository;

    public CompliancePolicy findById(Long id) {
        return compliancePolicyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("合规策略不存在: " + id));
    }

    public List<CompliancePolicy> findByOrganizationId(Long organizationId) {
        return compliancePolicyRepository.findByOrganizationId(organizationId);
    }

    @Transactional
    public CompliancePolicy createPolicy(Long organizationId, String name, String policyType, String configJson) {
        CompliancePolicy policy = CompliancePolicy.builder()
                .organizationId(organizationId)
                .name(name)
                .policyType(policyType)
                .configJson(configJson)
                .status("ACTIVE")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .createUser("system")
                .updateUser("system")
                .build();
        return compliancePolicyRepository.save(policy);
    }

    @Transactional
    public CompliancePolicy updatePolicy(Long id, String name, String configJson, String status) {
        CompliancePolicy policy = findById(id);
        if (name != null) policy.setName(name);
        if (configJson != null) policy.setConfigJson(configJson);
        if (status != null) policy.setStatus(status);
        policy.setUpdateTime(LocalDateTime.now());
        return compliancePolicyRepository.save(policy);
    }

    @Transactional
    public void deletePolicy(Long id) {
        compliancePolicyRepository.deleteById(id);
    }
}