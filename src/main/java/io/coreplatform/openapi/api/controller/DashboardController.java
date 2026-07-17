package io.coreplatform.openapi.api.controller;

import io.coreplatform.openapi.application.service.DashboardApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/openapi")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "仪表盘统计")
public class DashboardController {

    private final DashboardApplicationService dashboardApplicationService;

    @GetMapping("/dashboard")
    @Operation(summary = "获取仪表盘统计数据")
    public Map<String, Object> dashboard() {
        return dashboardApplicationService.getStats();
    }
}