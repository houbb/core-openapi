package io.coreplatform.openapi.portal.api.controller;

import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.portal.api.response.ApiCatalogDetailResponse;
import io.coreplatform.openapi.portal.api.response.ApiCatalogItemResponse;
import io.coreplatform.openapi.portal.application.service.ApiCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/portal/catalog")
@RequiredArgsConstructor
@Tag(name = "API Catalog", description = "API目录（公开）")
public class ApiCatalogController {

    private final ApiCatalogService catalogService;

    @GetMapping
    @Operation(summary = "获取API目录")
    public PageResult<ApiCatalogItemResponse> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        return catalogService.listCatalog(page, size, keyword, category);
    }

    @GetMapping("/{apiId}")
    @Operation(summary = "获取API详情")
    public ApiCatalogDetailResponse detail(@PathVariable Long apiId) {
        return catalogService.getApiDetail(apiId);
    }

    @GetMapping("/by-service/{serviceId}")
    @Operation(summary = "按服务获取API列表")
    public List<ApiCatalogItemResponse> listByService(@PathVariable Long serviceId) {
        return catalogService.listByService(serviceId);
    }
}