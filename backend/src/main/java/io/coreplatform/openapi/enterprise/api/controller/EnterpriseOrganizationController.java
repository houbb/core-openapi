package io.coreplatform.openapi.enterprise.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.enterprise.api.request.OrganizationRequest;
import io.coreplatform.openapi.enterprise.api.response.OrganizationResponse;
import io.coreplatform.openapi.enterprise.application.service.OrganizationService;
import io.coreplatform.openapi.enterprise.domain.Organization;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/openapi/enterprise/organizations")
@RequiredArgsConstructor
@Tag(name = "Enterprise Organization", description = "企业组织管理")
public class EnterpriseOrganizationController {

    private final OrganizationService organizationService;

    @GetMapping
    @Operation(summary = "获取组织列表")
    public PageResult<OrganizationResponse> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        IPage<Organization> result = organizationService.findPage(page, size, keyword, type, status);
        return PageResult.of(
                result.getRecords().stream().map(this::toResponse).toList(),
                page, size, result.getTotal());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取组织详情")
    public OrganizationResponse get(@PathVariable Long id) {
        return toResponse(organizationService.findById(id));
    }

    @PostMapping
    @Operation(summary = "创建组织")
    public OrganizationResponse create(@Valid @RequestBody OrganizationRequest request) {
        Organization org = organizationService.createOrganization(
                request.getName(), request.getType(), request.getOwnerId(),
                request.getDescription(), request.getContactEmail(),
                request.getContactPhone(), request.getWebsite());
        return toResponse(org);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新组织")
    public OrganizationResponse update(@PathVariable Long id, @Valid @RequestBody OrganizationRequest request) {
        Organization org = organizationService.updateOrganization(
                id, request.getName(), request.getDescription(), request.getContactEmail(),
                request.getContactPhone(), request.getWebsite(), request.getLogoUrl());
        return toResponse(org);
    }

    @PostMapping("/{id}/status")
    @Operation(summary = "更新组织状态")
    public OrganizationResponse updateStatus(@PathVariable Long id, @RequestParam String status) {
        return toResponse(organizationService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除组织")
    public void delete(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
    }

    private OrganizationResponse toResponse(Organization o) {
        return OrganizationResponse.builder()
                .id(o.getId()).name(o.getName()).code(o.getCode())
                .type(o.getType()).ownerId(o.getOwnerId()).status(o.getStatus())
                .description(o.getDescription()).logoUrl(o.getLogoUrl())
                .website(o.getWebsite()).contactEmail(o.getContactEmail())
                .contactPhone(o.getContactPhone()).tenantId(o.getTenantId())
                .createTime(o.getCreateTime()).updateTime(o.getUpdateTime())
                .build();
    }
}