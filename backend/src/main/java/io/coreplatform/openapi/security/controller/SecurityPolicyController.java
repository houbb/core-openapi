package io.coreplatform.openapi.security.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.api.request.SecurityPolicyRequest;
import io.coreplatform.openapi.api.response.SecurityPolicyResponse;
import io.coreplatform.openapi.security.domain.ApiSecurityPolicy;
import io.coreplatform.openapi.security.service.ApiSecurityPolicyApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/openapi/security/policies")
@RequiredArgsConstructor
@Tag(name = "Security Policy Management", description = "安全策略管理")
public class SecurityPolicyController {

    private final ApiSecurityPolicyApplicationService policyApplicationService;

    @GetMapping
    @Operation(summary = "获取安全策略列表")
    public PageResult<SecurityPolicyResponse> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "50") long size) {
        IPage<ApiSecurityPolicy> result = policyApplicationService.findPage(page, size);
        return PageResult.of(
                result.getRecords().stream().map(this::toResponse).toList(),
                page, size, result.getTotal()
        );
    }

    @GetMapping("/api/{apiId}")
    @Operation(summary = "根据API ID获取安全策略")
    public SecurityPolicyResponse getByApiId(@PathVariable Long apiId) {
        ApiSecurityPolicy policy = policyApplicationService.findByApiId(apiId)
                .orElse(null);
        return policy != null ? toResponse(policy) : null;
    }

    @PostMapping
    @Operation(summary = "创建或更新安全策略")
    public SecurityPolicyResponse createOrUpdate(@Valid @RequestBody SecurityPolicyRequest request) {
        ApiSecurityPolicy policy = policyApplicationService.createOrUpdatePolicy(
                request.getApiId(),
                request.getRequiredPermission(),
                request.getAuthRequired(),
                request.getIpWhiteList(),
                request.getTimeLimitEnabled(),
                request.getTimeLimitStart(),
                request.getTimeLimitEnd(),
                request.getTenantCheck()
        );
        return toResponse(policy);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除安全策略")
    public void delete(@PathVariable Long id) {
        policyApplicationService.deleteById(id);
    }

    private SecurityPolicyResponse toResponse(ApiSecurityPolicy policy) {
        return SecurityPolicyResponse.builder()
                .id(policy.getId())
                .apiId(policy.getApiId())
                .requiredPermission(policy.getRequiredPermission())
                .authRequired(policy.getAuthRequired())
                .signRequired(policy.getSignRequired())
                .ipWhiteList(policy.getIpWhiteList())
                .timeLimitEnabled(policy.getTimeLimitEnabled())
                .timeLimitStart(policy.getTimeLimitStart())
                .timeLimitEnd(policy.getTimeLimitEnd())
                .tenantCheck(policy.getTenantCheck())
                .status(policy.getStatus())
                .createTime(policy.getCreateTime())
                .updateTime(policy.getUpdateTime())
                .build();
    }
}