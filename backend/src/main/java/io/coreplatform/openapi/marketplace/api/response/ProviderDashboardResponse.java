package io.coreplatform.openapi.marketplace.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderDashboardResponse {
    private Long providerId;
    private String providerName;
    private Integer totalProducts;
    private Long totalRequests;
    private Long totalTokens;
    private Long totalErrors;
    private Long totalUsers;
    private List<Map<String, Object>> productStats;
}