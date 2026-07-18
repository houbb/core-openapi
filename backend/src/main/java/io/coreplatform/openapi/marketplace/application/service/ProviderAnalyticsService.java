package io.coreplatform.openapi.marketplace.application.service;

import io.coreplatform.openapi.marketplace.domain.ApiProduct;
import io.coreplatform.openapi.marketplace.domain.ApiUsageRecord;
import io.coreplatform.openapi.marketplace.port.ApiProductRepository;
import io.coreplatform.openapi.marketplace.port.ApiProviderRepository;
import io.coreplatform.openapi.marketplace.port.ApiUsageRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProviderAnalyticsService {

    private final ApiUsageRecordRepository usageRecordRepository;
    private final ApiProductRepository productRepository;
    private final ApiProviderRepository providerRepository;

    public Map<String, Object> getProviderDashboard(Long providerId) {
        var provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider 不存在: " + providerId));

        List<ApiProduct> products = productRepository.findByProviderId(providerId);
        Long totalRequests = 0L;
        Long totalTokens = 0L;
        Long totalErrors = 0L;
        Set<Long> distinctUsers = new HashSet<>();

        for (ApiProduct product : products) {
            totalRequests += usageRecordRepository.sumRequestCountByProductId(product.getId());
            distinctUsers.addAll(
                    usageRecordRepository.findByProductIdAndDateRange(product.getId(), null, null)
                            .stream().map(ApiUsageRecord::getUserId).toList());
        }

        // Aggregate from all products
        List<Map<String, Object>> productStats = new ArrayList<>();
        for (ApiProduct product : products) {
            Long requests = usageRecordRepository.sumRequestCountByProductId(product.getId());
            Long users = usageRecordRepository.countDistinctUsersByProductId(product.getId());
            Map<String, Object> stat = new LinkedHashMap<>();
            stat.put("productId", product.getId());
            stat.put("productName", product.getName());
            stat.put("requests", requests);
            stat.put("users", users);
            productStats.add(stat);
        }

        Map<String, Object> dashboard = new LinkedHashMap<>();
        dashboard.put("providerId", provider.getId());
        dashboard.put("providerName", provider.getName());
        dashboard.put("totalProducts", products.size());
        dashboard.put("totalRequests", totalRequests);
        dashboard.put("totalTokens", totalTokens);
        dashboard.put("totalErrors", totalErrors);
        dashboard.put("totalUsers", distinctUsers.size());
        dashboard.put("productStats", productStats);
        return dashboard;
    }

    public Map<String, Object> getProductAnalytics(Long productId) {
        Long totalRequests = usageRecordRepository.sumRequestCountByProductId(productId);
        Long totalUsers = usageRecordRepository.countDistinctUsersByProductId(productId);
        List<ApiUsageRecord> recentRecords = usageRecordRepository.findByProductIdAndDateRange(
                productId, java.time.LocalDate.now().minusDays(30), java.time.LocalDate.now());

        Map<String, Object> analytics = new LinkedHashMap<>();
        analytics.put("productId", productId);
        analytics.put("totalRequests", totalRequests);
        analytics.put("totalUsers", totalUsers);
        analytics.put("dailyStats", recentRecords);
        return analytics;
    }
}