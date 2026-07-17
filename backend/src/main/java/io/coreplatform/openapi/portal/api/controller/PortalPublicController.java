package io.coreplatform.openapi.portal.api.controller;

import io.coreplatform.openapi.portal.api.response.DocumentResponse;
import io.coreplatform.openapi.portal.api.response.SdkCenterItemResponse;
import io.coreplatform.openapi.portal.api.response.UsageStatsResponse;
import io.coreplatform.openapi.portal.application.service.PortalDocumentService;
import io.coreplatform.openapi.portal.application.service.SdkCenterService;
import io.coreplatform.openapi.portal.application.service.UsageAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Portal Public", description = "Developer Portal公开接口")
public class PortalPublicController {

    private final PortalDocumentService documentService;
    private final SdkCenterService sdkCenterService;
    private final UsageAnalyticsService analyticsService;

    // ---- Documentation ----
    @GetMapping("/api/v1/portal/docs/category/{category}")
    @Operation(summary = "按分类获取文档")
    public List<DocumentResponse> docsByCategory(@PathVariable String category) {
        return documentService.listByCategory(category);
    }

    @GetMapping("/api/v1/portal/docs/{slug}")
    @Operation(summary = "获取文档详情")
    public DocumentResponse docBySlug(@PathVariable String slug) {
        return documentService.getBySlug(slug);
    }

    @GetMapping("/api/v1/portal/docs")
    @Operation(summary = "获取所有公开文档")
    public List<DocumentResponse> allDocs() {
        return documentService.listAll();
    }

    // ---- SDK Center ----
    @GetMapping("/api/v1/portal/sdk")
    @Operation(summary = "获取SDK列表")
    public List<SdkCenterItemResponse> listSdks() {
        return sdkCenterService.listAvailableSdks();
    }

    @GetMapping("/api/v1/portal/sdk/{projectId}")
    @Operation(summary = "获取SDK详情")
    public SdkCenterItemResponse sdkDetail(@PathVariable Long projectId) {
        return sdkCenterService.getSdkDetail(projectId);
    }

    @GetMapping("/api/v1/portal/sdk/download/{genId}")
    @Operation(summary = "下载SDK")
    public ResponseEntity<Resource> downloadSdk(@PathVariable Long genId) {
        return sdkCenterService.download(genId);
    }

    // ---- Analytics ----
    @GetMapping("/api/v1/portal/analytics/overview")
    @Operation(summary = "获取用量概览")
    public UsageStatsResponse analyticsOverview(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("portalUserId");
        return analyticsService.getOverview(userId);
    }
}