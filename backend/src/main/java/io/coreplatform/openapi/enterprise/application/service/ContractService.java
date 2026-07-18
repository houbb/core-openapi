package io.coreplatform.openapi.enterprise.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.enterprise.domain.Contract;
import io.coreplatform.openapi.enterprise.port.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;

    public IPage<Contract> findPage(long page, long size, Long organizationId, String status) {
        return contractRepository.findPage(page, size, organizationId, status);
    }

    public Contract findById(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("合同不存在: " + id));
    }

    @Transactional
    public Contract createContract(Long organizationId, String contractNo, String name, String planName,
                                    LocalDate startDate, LocalDate endDate, Long maxRequests, Integer maxQps,
                                    Integer supportsPhone, Integer supports724, String description) {
        Contract contract = Contract.builder()
                .organizationId(organizationId)
                .contractNo(contractNo)
                .name(name)
                .planName(planName)
                .startDate(startDate)
                .endDate(endDate)
                .status("DRAFT")
                .maxRequests(maxRequests != null ? maxRequests : 0)
                .maxQps(maxQps != null ? maxQps : 0)
                .supportsPhone(supportsPhone != null ? supportsPhone : 0)
                .supports724(supports724 != null ? supports724 : 0)
                .description(description)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .createUser("system")
                .updateUser("system")
                .build();
        return contractRepository.save(contract);
    }

    @Transactional
    public Contract updateContract(Long id, String name, String planName, LocalDate startDate, LocalDate endDate,
                                    Long maxRequests, Integer maxQps, Integer supportsPhone, Integer supports724,
                                    String description) {
        Contract contract = findById(id);
        if (name != null) contract.setName(name);
        if (planName != null) contract.setPlanName(planName);
        if (startDate != null) contract.setStartDate(startDate);
        if (endDate != null) contract.setEndDate(endDate);
        if (maxRequests != null) contract.setMaxRequests(maxRequests);
        if (maxQps != null) contract.setMaxQps(maxQps);
        if (supportsPhone != null) contract.setSupportsPhone(supportsPhone);
        if (supports724 != null) contract.setSupports724(supports724);
        if (description != null) contract.setDescription(description);
        contract.setUpdateTime(LocalDateTime.now());
        return contractRepository.save(contract);
    }

    @Transactional
    public Contract activateContract(Long id) {
        Contract contract = findById(id);
        contract.setStatus("ACTIVE");
        contract.setUpdateTime(LocalDateTime.now());
        return contractRepository.save(contract);
    }

    @Transactional
    public Contract expireContract(Long id) {
        Contract contract = findById(id);
        contract.setStatus("EXPIRED");
        contract.setUpdateTime(LocalDateTime.now());
        return contractRepository.save(contract);
    }

    @Transactional
    public void deleteContract(Long id) {
        contractRepository.deleteById(id);
    }

    public List<Contract> findByOrganizationId(Long organizationId) {
        return contractRepository.findByOrganizationId(organizationId);
    }

    public Long countByStatus(String status) {
        return contractRepository.countByStatus(status);
    }
}