package io.coreplatform.openapi.enterprise.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.enterprise.api.request.ContractRequest;
import io.coreplatform.openapi.enterprise.api.response.ContractResponse;
import io.coreplatform.openapi.enterprise.application.service.ContractService;
import io.coreplatform.openapi.enterprise.domain.Contract;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/openapi/enterprise/contracts")
@RequiredArgsConstructor
@Tag(name = "Enterprise Contract", description = "企业合同管理")
public class EnterpriseContractController {

    private final ContractService contractService;

    @GetMapping
    @Operation(summary = "获取合同列表")
    public PageResult<ContractResponse> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size,
            @RequestParam(required = false) Long organizationId,
            @RequestParam(required = false) String status) {
        IPage<Contract> result = contractService.findPage(page, size, organizationId, status);
        return PageResult.of(
                result.getRecords().stream().map(this::toResponse).toList(),
                page, size, result.getTotal());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取合同详情")
    public ContractResponse get(@PathVariable Long id) {
        return toResponse(contractService.findById(id));
    }

    @PostMapping
    @Operation(summary = "创建合同")
    public ContractResponse create(@Valid @RequestBody ContractRequest request) {
        Contract contract = contractService.createContract(
                null, request.getContractNo(), request.getName(), request.getPlanName(),
                request.getStartDate(), request.getEndDate(), request.getMaxRequests(),
                request.getMaxQps(), request.getSupportsPhone(), request.getSupports724(),
                request.getDescription());
        return toResponse(contract);
    }

    @PostMapping("/{id}/activate")
    @Operation(summary = "激活合同")
    public ContractResponse activate(@PathVariable Long id) {
        return toResponse(contractService.activateContract(id));
    }

    @PostMapping("/{id}/expire")
    @Operation(summary = "到期合同")
    public ContractResponse expire(@PathVariable Long id) {
        return toResponse(contractService.expireContract(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新合同")
    public ContractResponse update(@PathVariable Long id, @Valid @RequestBody ContractRequest request) {
        Contract contract = contractService.updateContract(
                id, request.getName(), request.getPlanName(), request.getStartDate(),
                request.getEndDate(), request.getMaxRequests(), request.getMaxQps(),
                request.getSupportsPhone(), request.getSupports724(), request.getDescription());
        return toResponse(contract);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除合同")
    public void delete(@PathVariable Long id) {
        contractService.deleteContract(id);
    }

    private ContractResponse toResponse(Contract c) {
        return ContractResponse.builder()
                .id(c.getId()).organizationId(c.getOrganizationId())
                .contractNo(c.getContractNo()).name(c.getName())
                .planName(c.getPlanName()).startDate(c.getStartDate())
                .endDate(c.getEndDate()).status(c.getStatus())
                .maxRequests(c.getMaxRequests()).maxQps(c.getMaxQps())
                .supportsPhone(c.getSupportsPhone()).supports724(c.getSupports724())
                .description(c.getDescription())
                .createTime(c.getCreateTime()).updateTime(c.getUpdateTime())
                .build();
    }
}