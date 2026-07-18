package io.coreplatform.openapi.enterprise.api.controller;

import io.coreplatform.openapi.enterprise.api.request.SlaPolicyRequest;
import io.coreplatform.openapi.enterprise.api.response.SlaPolicyResponse;
import io.coreplatform.openapi.enterprise.application.service.SlaPolicyService;
import io.coreplatform.openapi.enterprise.domain.SlaPolicy;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/openapi/enterprise/sla-policies")
@RequiredArgsConstructor
@Tag(name = "Enterprise SLA", description = "企业SLA策略管理")
public class EnterpriseSlaController {

    private final SlaPolicyService slaPolicyService;

    @GetMapping("/by-org/{organizationId}")
    @Operation(summary = "获取组织的SLA策略")
    public SlaPolicyResponse getByOrg(@PathVariable Long organizationId) {
        SlaPolicy policy = slaPolicyService.findByOrganizationId(organizationId);
        return policy != null ? toResponse(policy) : null;
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取SLA策略详情")
    public SlaPolicyResponse get(@PathVariable Long id) {
        return toResponse(slaPolicyService.findById(id));
    }

    @PostMapping
    @Operation(summary = "创建或更新SLA策略")
    public SlaPolicyResponse createOrUpdate(@Valid @RequestBody SlaPolicyRequest request,
                                             @RequestParam Long organizationId) {
        SlaPolicy policy = slaPolicyService.createOrUpdate(
                organizationId, request.getName(), request.getAvailability(),
                request.getResponseTimeMs(), request.getLatencyP99Ms(),
                request.getSupportLevel(), request.getIncidentResponseMin());
        return toResponse(policy);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除SLA策略")
    public void delete(@PathVariable Long id) {
        slaPolicyService.deleteSlaPolicy(id);
    }

    private SlaPolicyResponse toResponse(SlaPolicy p) {
        return SlaPolicyResponse.builder()
                .id(p.getId()).organizationId(p.getOrganizationId())
                .name(p.getName()).availability(p.getAvailability())
                .responseTimeMs(p.getResponseTimeMs()).latencyP99Ms(p.getLatencyP99Ms())
                .supportLevel(p.getSupportLevel()).incidentResponseMin(p.getIncidentResponseMin())
                .status(p.getStatus())
                .createTime(p.getCreateTime()).updateTime(p.getUpdateTime())
                .build();
    }
}