package io.coreplatform.openapi.marketplace.api.controller;

import io.coreplatform.openapi.marketplace.api.request.ListingRequest;
import io.coreplatform.openapi.marketplace.application.service.ApiListingService;
import io.coreplatform.openapi.marketplace.application.service.ApiProductService;
import io.coreplatform.openapi.marketplace.application.service.ApiProviderService;
import io.coreplatform.openapi.marketplace.application.service.ApiReviewService;
import io.coreplatform.openapi.marketplace.domain.ApiListing;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/openapi/marketplace/admin")
@RequiredArgsConstructor
@Tag(name = "Marketplace Admin", description = "Marketplace 管理后台")
public class MarketplaceAdminController {

    private final ApiListingService listingService;
    private final ApiReviewService reviewService;
    private final ApiProductService productService;
    private final ApiProviderService providerService;

    @GetMapping("/dashboard")
    @Operation(summary = "Marketplace 管理概览")
    public Map<String, Object> dashboard() {
        Map<String, Object> dashboard = new LinkedHashMap<>();
        dashboard.put("totalProducts", productService.countAll());
        dashboard.put("publishedProducts", productService.countByStatus("PUBLISHED"));
        dashboard.put("pendingReview", productService.countByStatus("PENDING_REVIEW"));
        dashboard.put("totalProviders", providerService.countAll());
        dashboard.put("verifiedProviders", providerService.countByStatus("ACTIVE"));
        return dashboard;
    }

    @PostMapping("/products/{productId}/listing")
    @Operation(summary = "设置商品上架信息")
    public Map<String, Object> saveListing(@PathVariable Long productId,
                                            @Valid @RequestBody ListingRequest request) {
        ApiListing listing = listingService.saveListing(
                productId, request.getFeatured(), request.getSortOrder(),
                request.getTags(), request.getHighlightText());
        return Map.of("id", listing.getId(), "productId", listing.getProductId(),
                "featured", listing.getFeatured(), "tags", listing.getTags());
    }

    @GetMapping("/featured")
    @Operation(summary = "获取推荐列表")
    public List<Map<String, Object>> featured() {
        return listingService.findFeatured(10).stream()
                .map(l -> Map.of("productId", l.getProductId(), "tags", (Object) l.getTags(),
                        "highlightText", l.getHighlightText(), "sortOrder", l.getSortOrder()))
                .collect(Collectors.toList());
    }
}