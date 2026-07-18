package io.coreplatform.openapi.enterprise.api.controller;

import io.coreplatform.openapi.enterprise.api.request.IdentityProviderRequest;
import io.coreplatform.openapi.enterprise.api.response.IdentityProviderResponse;
import io.coreplatform.openapi.enterprise.application.service.IdentityProviderService;
import io.coreplatform.openapi.enterprise.domain.IdentityProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/openapi/enterprise/identity")
@RequiredArgsConstructor
@Tag(name = "Enterprise Identity", description = "企业身份管理")
public class EnterpriseIdentityController {

    private final IdentityProviderService identityProviderService;

    @GetMapping("/by-org/{organizationId}")
    @Operation(summary = "获取组织的身份提供商列表")
    public List<IdentityProviderResponse> listByOrg(@PathVariable Long organizationId) {
        return identityProviderService.findByOrganizationId(organizationId)
                .stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取身份提供商详情")
    public IdentityProviderResponse get(@PathVariable Long id) {
        return toResponse(identityProviderService.findById(id));
    }

    @PostMapping
    @Operation(summary = "创建身份提供商")
    public IdentityProviderResponse create(@Valid @RequestBody IdentityProviderRequest request,
                                            @RequestParam Long organizationId) {
        IdentityProvider provider = identityProviderService.createProvider(
                organizationId, request.getProviderType(), request.getName(),
                request.getConfigJson(), request.getIsDefault());
        return toResponse(provider);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新身份提供商")
    public IdentityProviderResponse update(@PathVariable Long id, @Valid @RequestBody IdentityProviderRequest request) {
        IdentityProvider provider = identityProviderService.updateProvider(
                id, request.getName(), request.getConfigJson(), request.getIsDefault(), request.getStatus());
        return toResponse(provider);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除身份提供商")
    public void delete(@PathVariable Long id) {
        identityProviderService.deleteProvider(id);
    }

    private IdentityProviderResponse toResponse(IdentityProvider p) {
        return IdentityProviderResponse.builder()
                .id(p.getId()).organizationId(p.getOrganizationId())
                .providerType(p.getProviderType()).name(p.getName())
                .configJson(p.getConfigJson()).isDefault(p.getIsDefault())
                .status(p.getStatus())
                .createTime(p.getCreateTime()).updateTime(p.getUpdateTime())
                .build();
    }
}