package io.coreplatform.openapi.portal.api.controller;

import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.portal.api.response.DashboardStatsResponse;
import io.coreplatform.openapi.portal.application.service.DeveloperDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/portal/dashboard")
@RequiredArgsConstructor
@Tag(name = "Developer Dashboard", description = "开发者仪表盘")
public class DeveloperDashboardController {

    private final DeveloperDashboardService dashboardService;

    @GetMapping
    @Operation(summary = "获取仪表盘数据")
    public DashboardStatsResponse stats(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("portalUserId");
        return dashboardService.getStats(userId);
    }
}