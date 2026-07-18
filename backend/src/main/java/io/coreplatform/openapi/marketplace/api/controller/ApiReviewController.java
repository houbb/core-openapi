package io.coreplatform.openapi.marketplace.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.marketplace.api.request.ReviewRequest;
import io.coreplatform.openapi.marketplace.api.response.ReviewResponse;
import io.coreplatform.openapi.marketplace.application.service.ApiReviewService;
import io.coreplatform.openapi.marketplace.domain.ApiReview;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/openapi/marketplace/products/{productId}/reviews")
@RequiredArgsConstructor
@Tag(name = "API Review Management", description = "API 评价管理")
public class ApiReviewController {

    private final ApiReviewService reviewService;

    @GetMapping
    @Operation(summary = "获取商品评价列表")
    public PageResult<ReviewResponse> list(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size) {
        IPage<ApiReview> result = reviewService.findPage(page, size, productId);
        return PageResult.of(
                result.getRecords().stream().map(this::toResponse).toList(),
                page, size, result.getTotal());
    }

    @PostMapping
    @Operation(summary = "提交评价")
    public ReviewResponse create(@PathVariable Long productId,
                                  @RequestParam Long userId,
                                  @Valid @RequestBody ReviewRequest request) {
        ApiReview review = reviewService.createReview(
                productId, userId, request.getRating(), request.getComment());
        return toResponse(review);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除评价")
    public void delete(@PathVariable Long productId, @PathVariable Long id) {
        reviewService.deleteReview(id);
    }

    @GetMapping("/stats")
    @Operation(summary = "获取评价统计")
    public java.util.Map<String, Object> stats(@PathVariable Long productId) {
        return java.util.Map.of(
                "avgRating", reviewService.avgRatingByProductId(productId),
                "count", reviewService.countByProductId(productId));
    }

    private ReviewResponse toResponse(ApiReview r) {
        return ReviewResponse.builder()
                .id(r.getId()).productId(r.getProductId()).userId(r.getUserId())
                .rating(r.getRating()).comment(r.getComment())
                .createTime(r.getCreateTime()).build();
    }
}