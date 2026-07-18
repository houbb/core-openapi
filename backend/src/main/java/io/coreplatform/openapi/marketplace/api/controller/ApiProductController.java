package io.coreplatform.openapi.marketplace.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.marketplace.api.request.ProductRequest;
import io.coreplatform.openapi.marketplace.api.response.ProductResponse;
import io.coreplatform.openapi.marketplace.application.service.ApiProductService;
import io.coreplatform.openapi.marketplace.domain.ApiProduct;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/openapi/marketplace/products")
@RequiredArgsConstructor
@Tag(name = "Marketplace Product Management", description = "API 商品管理")
public class ApiProductController {

    private final ApiProductService productService;

    @GetMapping
    @Operation(summary = "获取商品列表")
    public PageResult<ProductResponse> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long providerId) {
        IPage<ApiProduct> result = productService.findPage(page, size, keyword, category, status, providerId);
        return PageResult.of(
                result.getRecords().stream().map(this::toResponse).toList(),
                page, size, result.getTotal());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取商品详情")
    public ProductResponse get(@PathVariable Long id) {
        return toResponse(productService.findById(id));
    }

    @PostMapping
    @Operation(summary = "创建商品")
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        ApiProduct product = productService.createProduct(
                request.getName(), request.getDescription(), request.getProviderId(),
                request.getApiId(), request.getCategory());
        return toResponse(product);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新商品")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        ApiProduct product = productService.updateProduct(
                id, request.getName(), request.getDescription(), request.getApiId(),
                request.getCategory(), request.getIconUrl());
        return toResponse(product);
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "发布商品")
    public ProductResponse publish(@PathVariable Long id) {
        return toResponse(productService.publishProduct(id));
    }

    @PostMapping("/{id}/deprecate")
    @Operation(summary = "下架商品")
    public ProductResponse deprecate(@PathVariable Long id) {
        return toResponse(productService.deprecateProduct(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品")
    public void delete(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    private ProductResponse toResponse(ApiProduct p) {
        return ProductResponse.builder()
                .id(p.getId()).name(p.getName()).description(p.getDescription())
                .providerId(p.getProviderId()).apiId(p.getApiId())
                .category(p.getCategory()).iconUrl(p.getIconUrl())
                .status(p.getStatus()).createTime(p.getCreateTime()).updateTime(p.getUpdateTime())
                .build();
    }
}