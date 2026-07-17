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
public class DashboardStatsResponse {
    private long applicationCount;
    private long apiKeyCount;
    private long subscriptionCount;
    private long todayCalls;
    private double successRate;
    private List<RecentActivity> recentActivities;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentActivity {
        private String title;
        private String description;
        private String time;
    }
}