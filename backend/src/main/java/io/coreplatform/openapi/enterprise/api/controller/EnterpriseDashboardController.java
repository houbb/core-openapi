package io.coreplatform.openapi.enterprise.api.controller;

import io.coreplatform.openapi.enterprise.api.response.EnterpriseDashboardResponse;
import io.coreplatform.openapi.enterprise.application.service.EnterpriseDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/openapi/enterprise")
@RequiredArgsConstructor
@Tag(name = "Enterprise Dashboard", description = "企业控制台")
public class EnterpriseDashboardController {

    private final EnterpriseDashboardService dashboardService;

    @GetMapping("/dashboard")
    @Operation(summary = "获取企业平台总览数据")
    public EnterpriseDashboardResponse getDashboard() {
        Map<String, Object> data = dashboardService.getDashboard();
        return EnterpriseDashboardResponse.builder()
                .totalOrganizations((Long) data.get("totalOrganizations"))
                .activeOrganizations((Long) data.get("activeOrganizations"))
                .totalPartners((Long) data.get("totalPartners"))
                .strategicPartners((Long) data.get("strategicPartners"))
                .activeContracts((Long) data.get("activeContracts"))
                .draftContracts((Long) data.get("draftContracts"))
                .details(data)
                .build();
    }

    @GetMapping("/dashboard/organization/{organizationId}")
    @Operation(summary = "获取组织维度概览")
    public Map<String, Object> getOrgDashboard(@PathVariable Long organizationId) {
        return dashboardService.getOrganizationDashboard(organizationId);
    }
}