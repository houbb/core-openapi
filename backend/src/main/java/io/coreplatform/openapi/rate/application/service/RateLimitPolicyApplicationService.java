package io.coreplatform.openapi.rate.application.service;

import io.coreplatform.openapi.rate.application.domain.RateLimitPolicy;
import io.coreplatform.openapi.rate.application.port.RateLimitPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RateLimitPolicyApplicationService {

    private final RateLimitPolicyRepository policyRepository;

    public List<RateLimitPolicy> listAll() {
        return policyRepository.findAll();
    }

    public List<RateLimitPolicy> listActive() {
        return policyRepository.findByStatus("ACTIVE");
    }

    public Optional<RateLimitPolicy> getById(Long id) {
        return policyRepository.findById(id);
    }

    @Transactional
    public RateLimitPolicy create(RateLimitPolicy policy) {
        policy.setId(null);
        policy.setStatus("ACTIVE");
        policy.setCreateTime(LocalDateTime.now());
        policy.setUpdateTime(LocalDateTime.now());
        if (policy.getAlgorithm() == null || policy.getAlgorithm().isBlank()) {
            policy.setAlgorithm("TOKEN_BUCKET");
        }
        if (policy.getRefillRate() == null) policy.setRefillRate(10);
        if (policy.getRefillPeriod() == null) policy.setRefillPeriod(1);
        return policyRepository.save(policy);
    }

    @Transactional
    public RateLimitPolicy update(Long id, RateLimitPolicy updated) {
        RateLimitPolicy existing = policyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Policy not found: " + id));
        existing.setName(updated.getName());
        existing.setScope(updated.getScope());
        existing.setScopeId(updated.getScopeId());
        existing.setLimitValue(updated.getLimitValue());
        existing.setRefillRate(updated.getRefillRate());
        existing.setRefillPeriod(updated.getRefillPeriod());
        existing.setDescription(updated.getDescription());
        existing.setUpdateTime(LocalDateTime.now());
        return policyRepository.save(existing);
    }

    @Transactional
    public void disable(Long id) {
        RateLimitPolicy existing = policyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Policy not found: " + id));
        existing.setStatus("DISABLED");
        existing.setUpdateTime(LocalDateTime.now());
        policyRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        policyRepository.deleteById(id);
    }
}