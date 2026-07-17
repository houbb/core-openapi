package io.coreplatform.openapi.api.controller;

import io.coreplatform.openapi.application.service.GatewayDashboardApplicationService;
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
@Tag(name = "Gateway Dashboard", description = "网关仪表盘")
public class GatewayDashboardController {

    private final GatewayDashboardApplicationService gatewayDashboardApplicationService;

    @GetMapping("/gateway/dashboard")
    @Operation(summary = "获取网关仪表盘统计数据")
    public Map<String, Object> dashboard() {
        return gatewayDashboardApplicationService.getStats();
    }
}