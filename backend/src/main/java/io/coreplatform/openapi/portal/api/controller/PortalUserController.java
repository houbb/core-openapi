package io.coreplatform.openapi.portal.api.controller;

import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.portal.api.request.FeedbackRequest;
import io.coreplatform.openapi.portal.api.request.SettingUpdateRequest;
import io.coreplatform.openapi.portal.api.response.FeedbackResponse;
import io.coreplatform.openapi.portal.api.response.NotificationResponse;
import io.coreplatform.openapi.portal.application.domain.DeveloperSetting;
import io.coreplatform.openapi.portal.application.service.DeveloperNotificationService;
import io.coreplatform.openapi.portal.application.service.DeveloperSettingService;
import io.coreplatform.openapi.portal.application.service.PortalFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Portal User", description = "开发者个人功能")
public class PortalUserController {

    private final DeveloperNotificationService notificationService;
    private final DeveloperSettingService settingService;
    private final PortalFeedbackService feedbackService;

    // ---- Notifications ----
    @GetMapping("/api/v1/portal/notifications")
    @Operation(summary = "获取通知列表")
    public PageResult<NotificationResponse> notifications(HttpServletRequest request,
                                                           @RequestParam(defaultValue = "1") long page,
                                                           @RequestParam(defaultValue = "20") long size) {
        Long userId = (Long) request.getAttribute("portalUserId");
        return notificationService.listNotifications(userId, page, size);
    }

    @PostMapping("/api/v1/portal/notifications/{id}/read")
    @Operation(summary = "标记通知已读")
    public void markRead(@PathVariable Long id) {
        notificationService.markRead(id);
    }

    @PostMapping("/api/v1/portal/notifications/read-all")
    @Operation(summary = "标记全部已读")
    public void markAllRead(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("portalUserId");
        notificationService.markAllRead(userId);
    }

    @GetMapping("/api/v1/portal/notifications/unread-count")
    @Operation(summary = "获取未读通知数")
    public Long unreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("portalUserId");
        return notificationService.getUnreadCount(userId);
    }

    // ---- Settings ----
    @GetMapping("/api/v1/portal/settings")
    @Operation(summary = "获取设置")
    public DeveloperSetting getSettings(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("portalUserId");
        return settingService.getSettings(userId);
    }

    @PutMapping("/api/v1/portal/settings")
    @Operation(summary = "更新设置")
    public DeveloperSetting updateSettings(HttpServletRequest request,
                                            @Valid @RequestBody SettingUpdateRequest req) {
        Long userId = (Long) request.getAttribute("portalUserId");
        return settingService.updateSettings(userId, req);
    }

    // ---- Feedback ----
    @PostMapping("/api/v1/portal/feedback")
    @Operation(summary = "提交反馈")
    public FeedbackResponse submitFeedback(HttpServletRequest request,
                                            @Valid @RequestBody FeedbackRequest req) {
        Long userId = (Long) request.getAttribute("portalUserId");
        return feedbackService.submit(userId, req);
    }

    @GetMapping("/api/v1/portal/feedback/my")
    @Operation(summary = "获取我的反馈")
    public PageResult<FeedbackResponse> myFeedback(HttpServletRequest request,
                                                    @RequestParam(defaultValue = "1") long page,
                                                    @RequestParam(defaultValue = "20") long size) {
        Long userId = (Long) request.getAttribute("portalUserId");
        return feedbackService.listByUser(userId, page, size);
    }
}