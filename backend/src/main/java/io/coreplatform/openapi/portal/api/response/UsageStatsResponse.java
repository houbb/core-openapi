package io.coreplatform.openapi.portal.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsageStatsResponse {
    private long totalCalls;
    private long todayCalls;
    private long weekCalls;
    private long monthCalls;
    private double avgLatencyMs;
    private long errorCount;
    private double errorRate;
    private List<ApiUsageItem> byApi;
    private List<ChartPoint> chart;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiUsageItem {
        private String apiName;
        private long callCount;
        private double avgLatencyMs;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartPoint {
        private String date;
        private long count;
    }
}