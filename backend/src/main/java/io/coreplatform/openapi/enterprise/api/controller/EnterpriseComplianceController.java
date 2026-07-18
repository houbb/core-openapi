package io.coreplatform.openapi.enterprise.api.controller;

import io.coreplatform.openapi.enterprise.api.request.CompliancePolicyRequest;
import io.coreplatform.openapi.enterprise.api.response.CompliancePolicyResponse;
import io.coreplatform.openapi.enterprise.application.service.CompliancePolicyService;
import io.coreplatform.openapi.enterprise.domain.CompliancePolicy;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/openapi/enterprise/compliance")
@RequiredArgsConstructor
@Tag(name = "Enterprise Compliance", description = "企业合规管理")
public class EnterpriseComplianceController {

    private final CompliancePolicyService compliancePolicyService;

    @GetMapping("/by-org/{organizationId}")
    @Operation(summary = "获取组织的合规策略列表")
    public List<CompliancePolicyResponse> listByOrg(@PathVariable Long organizationId) {
        return compliancePolicyService.findByOrganizationId(organizationId)
                .stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取合规策略详情")
    public CompliancePolicyResponse get(@PathVariable Long id) {
        return toResponse(compliancePolicyService.findById(id));
    }

    @PostMapping
    @Operation(summary = "创建合规策略")
    public CompliancePolicyResponse create(@Valid @RequestBody CompliancePolicyRequest request,
                                            @RequestParam Long organizationId) {
        CompliancePolicy policy = compliancePolicyService.createPolicy(
                organizationId, request.getName(), request.getPolicyType(), request.getConfigJson());
        return toResponse(policy);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新合规策略")
    public CompliancePolicyResponse update(@PathVariable Long id, @Valid @RequestBody CompliancePolicyRequest request) {
        CompliancePolicy policy = compliancePolicyService.updatePolicy(
                id, request.getName(), request.getConfigJson(), request.getStatus());
        return toResponse(policy);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除合规策略")
    public void delete(@PathVariable Long id) {
        compliancePolicyService.deletePolicy(id);
    }

    private CompliancePolicyResponse toResponse(CompliancePolicy p) {
        return CompliancePolicyResponse.builder()
                .id(p.getId()).organizationId(p.getOrganizationId())
                .name(p.getName()).policyType(p.getPolicyType())
                .configJson(p.getConfigJson()).status(p.getStatus())
                .createTime(p.getCreateTime()).updateTime(p.getUpdateTime())
                .build();
    }
}