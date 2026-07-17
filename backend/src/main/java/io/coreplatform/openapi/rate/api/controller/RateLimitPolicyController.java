package io.coreplatform.openapi.rate.api.controller;

import io.coreplatform.openapi.rate.application.domain.RateLimitPolicy;
import io.coreplatform.openapi.rate.application.service.RateLimitPolicyApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/openapi/rate-limit/policies")
@RequiredArgsConstructor
@Tag(name = "Rate Limit Policy", description = "限流策略管理")
public class RateLimitPolicyController {

    private final RateLimitPolicyApplicationService policyService;

    @GetMapping
    @Operation(summary = "获取所有限流策略")
    public List<RateLimitPolicy> listAll() {
        return policyService.listAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取单个限流策略")
    public RateLimitPolicy getById(@PathVariable Long id) {
        return policyService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Policy not found: " + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "创建限流策略")
    public RateLimitPolicy create(@RequestBody RateLimitPolicy policy) {
        return policyService.create(policy);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新限流策略")
    public RateLimitPolicy update(@PathVariable Long id, @RequestBody RateLimitPolicy policy) {
        return policyService.update(id, policy);
    }

    @PostMapping("/{id}/disable")
    @Operation(summary = "禁用限流策略")
    public void disable(@PathVariable Long id) {
        policyService.disable(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "删除限流策略")
    public void delete(@PathVariable Long id) {
        policyService.delete(id);
    }
}