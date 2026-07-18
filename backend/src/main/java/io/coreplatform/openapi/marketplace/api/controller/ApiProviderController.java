package io.coreplatform.openapi.marketplace.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.marketplace.api.request.ProviderRequest;
import io.coreplatform.openapi.marketplace.api.response.ProviderResponse;
import io.coreplatform.openapi.marketplace.application.service.ApiProviderService;
import io.coreplatform.openapi.marketplace.domain.ApiProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/openapi/marketplace/providers")
@RequiredArgsConstructor
@Tag(name = "Marketplace Provider Management", description = "API 提供者管理")
public class ApiProviderController {

    private final ApiProviderService providerService;

    @GetMapping
    @Operation(summary = "获取 Provider 列表")
    public PageResult<ProviderResponse> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        IPage<ApiProvider> result = providerService.findPage(page, size, keyword, type, status);
        return PageResult.of(
                result.getRecords().stream().map(this::toResponse).toList(),
                page, size, result.getTotal());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取 Provider 详情")
    public ProviderResponse get(@PathVariable Long id) {
        return toResponse(providerService.findById(id));
    }

    @PostMapping
    @Operation(summary = "创建 Provider")
    public ProviderResponse create(@Valid @RequestBody ProviderRequest request) {
        ApiProvider provider = providerService.createProvider(
                request.getName(), request.getDescription(), request.getType(),
                request.getOwnerId(), request.getContactEmail(), request.getWebsite());
        return toResponse(provider);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新 Provider")
    public ProviderResponse update(@PathVariable Long id, @Valid @RequestBody ProviderRequest request) {
        ApiProvider provider = providerService.updateProvider(
                id, request.getName(), request.getDescription(),
                request.getContactEmail(), request.getWebsite(), request.getLogoUrl());
        return toResponse(provider);
    }

    @PostMapping("/{id}/verify")
    @Operation(summary = "认证 Provider")
    public ProviderResponse verify(@PathVariable Long id) {
        return toResponse(providerService.verifyProvider(id));
    }

    @PostMapping("/{id}/status")
    @Operation(summary = "切换 Provider 状态")
    public ProviderResponse toggleStatus(@PathVariable Long id, @RequestParam String status) {
        return toResponse(providerService.toggleStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除 Provider")
    public void delete(@PathVariable Long id) {
        providerService.deleteProvider(id);
    }

    private ProviderResponse toResponse(ApiProvider p) {
        return ProviderResponse.builder()
                .id(p.getId()).name(p.getName()).description(p.getDescription())
                .type(p.getType()).ownerId(p.getOwnerId()).verified(p.getVerified())
                .status(p.getStatus()).contactEmail(p.getContactEmail())
                .website(p.getWebsite()).logoUrl(p.getLogoUrl())
                .createTime(p.getCreateTime())
                .build();
    }
}