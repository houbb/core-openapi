package io.coreplatform.openapi.marketplace.application.service;

import io.coreplatform.openapi.marketplace.domain.ApiPlan;
import io.coreplatform.openapi.marketplace.port.ApiPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiPlanService {

    private final ApiPlanRepository planRepository;

    public List<ApiPlan> findByProductId(Long productId) {
        return planRepository.findByProductId(productId);
    }

    public ApiPlan findById(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("定价计划不存在: " + id));
    }

    @Transactional
    public ApiPlan createPlan(Long productId, String name, String description, BigDecimal price,
                               String billingType, String limitConfig, Integer sortOrder) {
        ApiPlan plan = ApiPlan.builder()
                .productId(productId)
                .name(name)
                .description(description)
                .price(price != null ? price : BigDecimal.ZERO)
                .billingType(billingType != null ? billingType : "FREE")
                .limitConfig(limitConfig)
                .status("ACTIVE")
                .sortOrder(sortOrder != null ? sortOrder : 0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        return planRepository.save(plan);
    }

    @Transactional
    public ApiPlan updatePlan(Long id, String name, String description, BigDecimal price,
                               String billingType, String limitConfig, Integer sortOrder, String status) {
        ApiPlan plan = findById(id);
        if (name != null) plan.setName(name);
        if (description != null) plan.setDescription(description);
        if (price != null) plan.setPrice(price);
        if (billingType != null) plan.setBillingType(billingType);
        if (limitConfig != null) plan.setLimitConfig(limitConfig);
        if (sortOrder != null) plan.setSortOrder(sortOrder);
        if (status != null) plan.setStatus(status);
        plan.setUpdateTime(LocalDateTime.now());
        return planRepository.save(plan);
    }

    @Transactional
    public void deletePlan(Long id) {
        planRepository.deleteById(id);
    }
}