package io.coreplatform.openapi.portal.application.service;

import io.coreplatform.openapi.portal.api.request.SubscriptionRequest;
import io.coreplatform.openapi.portal.api.response.SubscriptionResponse;
import io.coreplatform.openapi.portal.application.domain.ApiSubscription;
import io.coreplatform.openapi.portal.application.port.ApiSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApiSubscriptionService {

    private final ApiSubscriptionRepository subscriptionRepository;

    public List<SubscriptionResponse> listMySubscriptions(Long userId) {
        return subscriptionRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SubscriptionResponse subscribe(Long userId, SubscriptionRequest request) {
        // Check if already subscribed
        var existing = subscriptionRepository.findByUserAndApi(userId, request.getApiId());
        if (existing.isPresent() && "ACTIVE".equals(existing.get().getStatus())) {
            throw new IllegalArgumentException("已订阅此API");
        }

        ApiSubscription sub = ApiSubscription.builder()
                .userId(userId)
                .apiId(request.getApiId())
                .plan(request.getPlan() != null ? request.getPlan() : "FREE")
                .status("ACTIVE")
                .maxQps(100)
                .maxDaily(1000)
                .subscribedAt(LocalDateTime.now())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        ApiSubscription saved = subscriptionRepository.save(sub);
        return toResponse(saved);
    }

    @Transactional
    public void cancelSubscription(Long userId, Long subId) {
        ApiSubscription sub = subscriptionRepository.findById(subId)
                .orElseThrow(() -> new IllegalArgumentException("订阅不存在: " + subId));
        if (!userId.equals(sub.getUserId())) {
            throw new IllegalArgumentException("无权操作此订阅");
        }
        sub.setStatus("CANCELLED");
        sub.setUpdateTime(LocalDateTime.now());
        subscriptionRepository.save(sub);
    }

    private SubscriptionResponse toResponse(ApiSubscription sub) {
        return SubscriptionResponse.builder()
                .id(sub.getId())
                .apiId(sub.getApiId())
                .apiName("API #" + sub.getApiId())
                .plan(sub.getPlan())
                .status(sub.getStatus())
                .maxQps(sub.getMaxQps())
                .maxDaily(sub.getMaxDaily())
                .subscribedAt(sub.getSubscribedAt())
                .expiredAt(sub.getExpiredAt())
                .build();
    }
}