package io.coreplatform.openapi.portal.application.service;

import io.coreplatform.openapi.portal.api.response.UsageStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UsageAnalyticsService {

    public UsageStatsResponse getOverview(Long userId) {
        return UsageStatsResponse.builder()
                .totalCalls(0)
                .todayCalls(0)
                .weekCalls(0)
                .monthCalls(0)
                .avgLatencyMs(0)
                .errorCount(0)
                .errorRate(0)
                .byApi(Collections.emptyList())
                .chart(Collections.emptyList())
                .build();
    }
}