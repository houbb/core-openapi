package io.coreplatform.openapi.marketplace.api.controller;

import io.coreplatform.openapi.marketplace.application.service.ProviderAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/openapi/marketplace/provider")
@RequiredArgsConstructor
@Tag(name = "Provider Dashboard", description = "Provider 仪表盘")
public class ProviderDashboardController {

    private final ProviderAnalyticsService analyticsService;

    @GetMapping("/dashboard")
    @Operation(summary = "Provider 仪表盘")
    public Map<String, Object> dashboard(@RequestParam Long providerId) {
        return analyticsService.getProviderDashboard(providerId);
    }

    @GetMapping("/analytics/{productId}")
    @Operation(summary = "商品使用分析")
    public Map<String, Object> productAnalytics(@PathVariable Long productId) {
        return analyticsService.getProductAnalytics(productId);
    }
}