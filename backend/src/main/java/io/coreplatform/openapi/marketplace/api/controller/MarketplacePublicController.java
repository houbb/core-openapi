package io.coreplatform.openapi.marketplace.api.controller;

import io.coreplatform.openapi.marketplace.application.service.MarketplaceBrowseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/marketplace/public")
@RequiredArgsConstructor
@Tag(name = "Marketplace Public", description = "Marketplace 公开浏览")
public class MarketplacePublicController {

    private final MarketplaceBrowseService browseService;

    @GetMapping("/featured")
    @Operation(summary = "获取首页推荐商品")
    public List<Map<String, Object>> featured() {
        return browseService.browseFeatured();
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "按分类浏览商品")
    public List<Map<String, Object>> byCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "20") int limit) {
        return browseService.browseByCategory(category, limit);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索商品")
    public List<Map<String, Object>> search(@RequestParam String keyword) {
        return browseService.searchProducts(keyword);
    }

    @GetMapping("/products/{id}")
    @Operation(summary = "商品详情")
    public Map<String, Object> detail(@PathVariable Long id) {
        return browseService.buildProductDetail(id);
    }
}