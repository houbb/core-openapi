package io.coreplatform.openapi.portal.application.service;

import io.coreplatform.openapi.portal.api.response.DashboardStatsResponse;
import io.coreplatform.openapi.portal.application.port.ApiSubscriptionRepository;
import io.coreplatform.openapi.portal.application.port.DeveloperNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class DeveloperDashboardService {

    private final ApiSubscriptionRepository subscriptionRepository;
    private final DeveloperNotificationRepository notificationRepository;

    public DashboardStatsResponse getStats(Long userId) {
        long subscriptionCount = subscriptionRepository.countByUserId(userId);
        long unreadCount = notificationRepository.countUnreadByUserId(userId);

        return DashboardStatsResponse.builder()
                .applicationCount(0)
                .apiKeyCount(0)
                .subscriptionCount(subscriptionCount)
                .todayCalls(0)
                .successRate(99.9)
                .recentActivities(Collections.emptyList())
                .build();
    }
}