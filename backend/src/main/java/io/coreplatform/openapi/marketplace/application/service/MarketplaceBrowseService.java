package io.coreplatform.openapi.marketplace.application.service;

import io.coreplatform.openapi.marketplace.domain.ApiListing;
import io.coreplatform.openapi.marketplace.domain.ApiPlan;
import io.coreplatform.openapi.marketplace.domain.ApiProduct;
import io.coreplatform.openapi.marketplace.domain.ApiProvider;
import io.coreplatform.openapi.marketplace.port.ApiListingRepository;
import io.coreplatform.openapi.marketplace.port.ApiPlanRepository;
import io.coreplatform.openapi.marketplace.port.ApiProductRepository;
import io.coreplatform.openapi.marketplace.port.ApiProviderRepository;
import io.coreplatform.openapi.marketplace.port.ApiReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketplaceBrowseService {

    private final ApiProductRepository productRepository;
    private final ApiProviderRepository providerRepository;
    private final ApiPlanRepository planRepository;
    private final ApiReviewRepository reviewRepository;
    private final ApiListingRepository listingRepository;

    public Map<String, Object> buildProductDetail(Long productId) {
        ApiProduct product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在: " + productId));

        ApiProvider provider = providerRepository.findById(product.getProviderId()).orElse(null);
        List<ApiPlan> plans = planRepository.findByProductId(productId);
        Double avgRating = reviewRepository.avgRatingByProductId(productId);
        Long reviewCount = reviewRepository.countByProductId(productId);

        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("id", product.getId());
        detail.put("name", product.getName());
        detail.put("description", product.getDescription());
        detail.put("category", product.getCategory());
        detail.put("iconUrl", product.getIconUrl());
        detail.put("status", product.getStatus());
        detail.put("provider", provider != null ? Map.of(
                "id", provider.getId(),
                "name", provider.getName(),
                "type", provider.getType(),
                "verified", provider.getVerified()
        ) : null);
        detail.put("plans", plans.stream().map(p -> Map.of(
                "id", p.getId(),
                "name", p.getName(),
                "description", p.getDescription(),
                "price", p.getPrice(),
                "billingType", p.getBillingType(),
                "limitConfig", p.getLimitConfig()
        )).collect(Collectors.toList()));
        detail.put("avgRating", Math.round(avgRating * 10.0) / 10.0);
        detail.put("reviewCount", reviewCount);
        detail.put("createTime", product.getCreateTime());
        return detail;
    }

    public List<Map<String, Object>> browseFeatured() {
        List<ApiListing> listings = listingRepository.findFeatured(10);
        return listings.stream()
                .map(l -> enrichProductSummary(l.getProductId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> browseByCategory(String category, int limit) {
        List<ApiProduct> products = productRepository.findPublished(category, limit > 0 ? limit : 20);
        return products.stream()
                .map(p -> enrichProductSummary(p.getId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> searchProducts(String keyword) {
        var page = productRepository.findPage(1, 20, keyword, null, "PUBLISHED", null);
        return page.getRecords().stream()
                .map(p -> enrichProductSummary(p.getId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Map<String, Object> enrichProductSummary(Long productId) {
        ApiProduct product = productRepository.findById(productId).orElse(null);
        if (product == null) return null;

        ApiProvider provider = providerRepository.findById(product.getProviderId()).orElse(null);
        Double avgRating = reviewRepository.avgRatingByProductId(productId);
        Long reviewCount = reviewRepository.countByProductId(productId);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("id", product.getId());
        summary.put("name", product.getName());
        summary.put("description", product.getDescription());
        summary.put("category", product.getCategory());
        summary.put("iconUrl", product.getIconUrl());
        summary.put("providerName", provider != null ? provider.getName() : "Unknown");
        summary.put("providerType", provider != null ? provider.getType() : "");
        summary.put("verified", provider != null && provider.getVerified() == 1);
        summary.put("avgRating", Math.round(avgRating * 10.0) / 10.0);
        summary.put("reviewCount", reviewCount);
        return summary;
    }
}