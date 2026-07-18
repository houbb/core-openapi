package io.coreplatform.openapi.enterprise.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.enterprise.api.request.PartnerRequest;
import io.coreplatform.openapi.enterprise.api.response.PartnerResponse;
import io.coreplatform.openapi.enterprise.application.service.PartnerService;
import io.coreplatform.openapi.enterprise.domain.Partner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/openapi/enterprise/partners")
@RequiredArgsConstructor
@Tag(name = "Enterprise Partner", description = "合作伙伴管理")
public class EnterprisePartnerController {

    private final PartnerService partnerService;

    @GetMapping
    @Operation(summary = "获取合作伙伴列表")
    public PageResult<PartnerResponse> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long organizationId) {
        IPage<Partner> result = partnerService.findPage(page, size, keyword, level, status, organizationId);
        return PageResult.of(
                result.getRecords().stream().map(this::toResponse).toList(),
                page, size, result.getTotal());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取合作伙伴详情")
    public PartnerResponse get(@PathVariable Long id) {
        return toResponse(partnerService.findById(id));
    }

    @PostMapping
    @Operation(summary = "创建合作伙伴")
    public PartnerResponse create(@Valid @RequestBody PartnerRequest request) {
        Partner partner = partnerService.createPartner(
                request.getOrganizationId(), request.getName(), request.getLevel(),
                request.getContactName(), request.getContactEmail(),
                request.getContactPhone(), request.getDescription());
        return toResponse(partner);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新合作伙伴")
    public PartnerResponse update(@PathVariable Long id, @Valid @RequestBody PartnerRequest request) {
        Partner partner = partnerService.updatePartner(
                id, request.getName(), request.getLevel(), request.getContactName(),
                request.getContactEmail(), request.getContactPhone(), request.getDescription());
        return toResponse(partner);
    }

    @PostMapping("/{id}/status")
    @Operation(summary = "更新合作伙伴状态")
    public PartnerResponse updateStatus(@PathVariable Long id, @RequestParam String status) {
        return toResponse(partnerService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除合作伙伴")
    public void delete(@PathVariable Long id) {
        partnerService.deletePartner(id);
    }

    private PartnerResponse toResponse(Partner p) {
        return PartnerResponse.builder()
                .id(p.getId()).organizationId(p.getOrganizationId())
                .name(p.getName()).level(p.getLevel()).status(p.getStatus())
                .contactName(p.getContactName()).contactEmail(p.getContactEmail())
                .contactPhone(p.getContactPhone()).description(p.getDescription())
                .createTime(p.getCreateTime()).updateTime(p.getUpdateTime())
                .build();
    }
}