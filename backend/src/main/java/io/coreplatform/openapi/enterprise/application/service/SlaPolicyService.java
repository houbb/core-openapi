package io.coreplatform.openapi.enterprise.application.service;

import io.coreplatform.openapi.enterprise.domain.SlaPolicy;
import io.coreplatform.openapi.enterprise.port.SlaPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SlaPolicyService {

    private final SlaPolicyRepository slaPolicyRepository;

    public SlaPolicy findById(Long id) {
        return slaPolicyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SLA策略不存在: " + id));
    }

    public SlaPolicy findByOrganizationId(Long organizationId) {
        return slaPolicyRepository.findByOrganizationId(organizationId)
                .orElse(null);
    }

    @Transactional
    public SlaPolicy createOrUpdate(Long organizationId, String name, BigDecimal availability,
                                     Integer responseTimeMs, Integer latencyP99Ms, String supportLevel,
                                     Integer incidentResponseMin) {
        SlaPolicy existing = slaPolicyRepository.findByOrganizationId(organizationId).orElse(null);

        if (existing != null) {
            if (name != null) existing.setName(name);
            if (availability != null) existing.setAvailability(availability);
            if (responseTimeMs != null) existing.setResponseTimeMs(responseTimeMs);
            if (latencyP99Ms != null) existing.setLatencyP99Ms(latencyP99Ms);
            if (supportLevel != null) existing.setSupportLevel(supportLevel);
            if (incidentResponseMin != null) existing.setIncidentResponseMin(incidentResponseMin);
            existing.setUpdateTime(LocalDateTime.now());
            return slaPolicyRepository.save(existing);
        }

        SlaPolicy policy = SlaPolicy.builder()
                .organizationId(organizationId)
                .name(name != null ? name : "Default SLA")
                .availability(availability != null ? availability : new BigDecimal("0.9900"))
                .responseTimeMs(responseTimeMs != null ? responseTimeMs : 1000)
                .latencyP99Ms(latencyP99Ms != null ? latencyP99Ms : 500)
                .supportLevel(supportLevel != null ? supportLevel : "STANDARD")
                .incidentResponseMin(incidentResponseMin != null ? incidentResponseMin : 60)
                .status("ACTIVE")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .createUser("system")
                .updateUser("system")
                .build();
        return slaPolicyRepository.save(policy);
    }

    @Transactional
    public void deleteSlaPolicy(Long id) {
        slaPolicyRepository.deleteById(id);
    }
}