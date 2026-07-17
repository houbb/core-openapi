package io.coreplatform.openapi.security.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.security.domain.ApiSecurityPolicy;
import io.coreplatform.openapi.security.port.ApiSecurityPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApiSecurityPolicyApplicationService {

    private final ApiSecurityPolicyRepository policyRepository;

    @Transactional
    public ApiSecurityPolicy createOrUpdatePolicy(Long apiId, String requiredPermission,
                                                   Integer authRequired, String ipWhiteList,
                                                   Integer timeLimitEnabled, String timeLimitStart,
                                                   String timeLimitEnd, Integer tenantCheck) {
        Optional<ApiSecurityPolicy> existing = policyRepository.findByApiId(apiId);
        ApiSecurityPolicy policy;
        if (existing.isPresent()) {
            policy = existing.get();
        } else {
            policy = ApiSecurityPolicy.builder().apiId(apiId).build();
        }

        policy.setRequiredPermission(requiredPermission != null ? requiredPermission : "");
        policy.setAuthRequired(authRequired != null ? authRequired : 1);
        policy.setSignRequired(0); // Not implemented yet
        policy.setIpWhiteList(ipWhiteList != null ? ipWhiteList : "");
        policy.setTimeLimitEnabled(timeLimitEnabled != null ? timeLimitEnabled : 0);
        policy.setTimeLimitStart(timeLimitStart != null ? timeLimitStart : "");
        policy.setTimeLimitEnd(timeLimitEnd != null ? timeLimitEnd : "");
        policy.setTenantCheck(tenantCheck != null ? tenantCheck : 0);
        policy.setStatus("ACTIVE");

        return policyRepository.save(policy);
    }

    public Optional<ApiSecurityPolicy> findByApiId(Long apiId) {
        return policyRepository.findByApiId(apiId);
    }

    public Optional<ApiSecurityPolicy> findById(Long id) {
        return policyRepository.findById(id);
    }

    public IPage<ApiSecurityPolicy> findPage(long page, long size) {
        return policyRepository.findPage(page, size);
    }

    @Transactional
    public void deleteById(Long id) {
        policyRepository.deleteById(id);
    }
}