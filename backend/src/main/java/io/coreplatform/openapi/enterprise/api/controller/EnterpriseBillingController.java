package io.coreplatform.openapi.enterprise.api.controller;

import io.coreplatform.openapi.enterprise.api.request.BillingAccountRequest;
import io.coreplatform.openapi.enterprise.api.response.BillingAccountResponse;
import io.coreplatform.openapi.enterprise.application.service.BillingAccountService;
import io.coreplatform.openapi.enterprise.domain.BillingAccount;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/openapi/enterprise/billing")
@RequiredArgsConstructor
@Tag(name = "Enterprise Billing", description = "企业计费管理")
public class EnterpriseBillingController {

    private final BillingAccountService billingAccountService;

    @GetMapping("/by-org/{organizationId}")
    @Operation(summary = "获取组织的计费账户")
    public BillingAccountResponse getByOrg(@PathVariable Long organizationId) {
        BillingAccount account = billingAccountService.findByOrganizationId(organizationId);
        return account != null ? toResponse(account) : null;
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取计费账户详情")
    public BillingAccountResponse get(@PathVariable Long id) {
        return toResponse(billingAccountService.findById(id));
    }

    @PostMapping
    @Operation(summary = "创建或获取计费账户")
    public BillingAccountResponse createOrGet(@Valid @RequestBody BillingAccountRequest request,
                                               @RequestParam Long organizationId) {
        BillingAccount account = billingAccountService.createOrGet(
                organizationId, request.getAccountName(), request.getCurrency());
        return toResponse(account);
    }

    @PostMapping("/{id}/status")
    @Operation(summary = "更新计费账户状态")
    public BillingAccountResponse updateStatus(@PathVariable Long id, @RequestParam String status) {
        return toResponse(billingAccountService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除计费账户")
    public void delete(@PathVariable Long id) {
        billingAccountService.deleteBillingAccount(id);
    }

    private BillingAccountResponse toResponse(BillingAccount a) {
        return BillingAccountResponse.builder()
                .id(a.getId()).organizationId(a.getOrganizationId())
                .accountName(a.getAccountName()).balance(a.getBalance())
                .currency(a.getCurrency()).status(a.getStatus())
                .createTime(a.getCreateTime()).updateTime(a.getUpdateTime())
                .build();
    }
}