package io.coreplatform.openapi.marketplace.api.controller;

import io.coreplatform.openapi.marketplace.api.request.PlanRequest;
import io.coreplatform.openapi.marketplace.api.response.PlanResponse;
import io.coreplatform.openapi.marketplace.application.service.ApiPlanService;
import io.coreplatform.openapi.marketplace.domain.ApiPlan;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/openapi/marketplace/products/{productId}/plans")
@RequiredArgsConstructor
@Tag(name = "API Plan Management", description = "定价计划管理")
public class ApiPlanController {

    private final ApiPlanService planService;

    @GetMapping
    @Operation(summary = "获取商品的定价计划列表")
    public List<PlanResponse> list(@PathVariable Long productId) {
        return planService.findByProductId(productId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @PostMapping
    @Operation(summary = "创建定价计划")
    public PlanResponse create(@PathVariable Long productId, @Valid @RequestBody PlanRequest request) {
        ApiPlan plan = planService.createPlan(
                productId, request.getName(), request.getDescription(), request.getPrice(),
                request.getBillingType(), request.getLimitConfig(), request.getSortOrder());
        return toResponse(plan);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新定价计划")
    public PlanResponse update(@PathVariable Long productId, @PathVariable Long id,
                                @Valid @RequestBody PlanRequest request) {
        ApiPlan plan = planService.updatePlan(
                id, request.getName(), request.getDescription(), request.getPrice(),
                request.getBillingType(), request.getLimitConfig(), request.getSortOrder(), null);
        return toResponse(plan);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除定价计划")
    public void delete(@PathVariable Long productId, @PathVariable Long id) {
        planService.deletePlan(id);
    }

    private PlanResponse toResponse(ApiPlan p) {
        return PlanResponse.builder()
                .id(p.getId()).productId(p.getProductId()).name(p.getName())
                .description(p.getDescription()).price(p.getPrice())
                .billingType(p.getBillingType()).limitConfig(p.getLimitConfig())
                .status(p.getStatus()).sortOrder(p.getSortOrder())
                .createTime(p.getCreateTime()).build();
    }
}