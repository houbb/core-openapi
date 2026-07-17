package io.coreplatform.openapi.portal.api.controller;

import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.portal.api.request.SubscriptionRequest;
import io.coreplatform.openapi.portal.api.response.SubscriptionResponse;
import io.coreplatform.openapi.portal.application.service.ApiSubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/portal/subscriptions")
@RequiredArgsConstructor
@Tag(name = "API Subscription", description = "API订阅管理")
public class ApiSubscriptionController {

    private final ApiSubscriptionService subscriptionService;

    @GetMapping
    @Operation(summary = "获取我的订阅列表")
    public List<SubscriptionResponse> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("portalUserId");
        return subscriptionService.listMySubscriptions(userId);
    }

    @PostMapping
    @Operation(summary = "订阅API")
    public SubscriptionResponse subscribe(HttpServletRequest request,
                                           @Valid @RequestBody SubscriptionRequest req) {
        Long userId = (Long) request.getAttribute("portalUserId");
        return subscriptionService.subscribe(userId, req);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消订阅")
    public void cancel(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("portalUserId");
        subscriptionService.cancelSubscription(userId, id);
    }
}