package io.coreplatform.openapi.portal.application.service;

import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.portal.api.response.NotificationResponse;
import io.coreplatform.openapi.portal.application.domain.DeveloperNotification;
import io.coreplatform.openapi.portal.application.port.DeveloperNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeveloperNotificationService {

    private final DeveloperNotificationRepository notificationRepository;

    public PageResult<NotificationResponse> listNotifications(Long userId, long page, long size) {
        var notifPage = notificationRepository.findByUserId(page, size, userId);
        List<NotificationResponse> items = notifPage.getRecords().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return PageResult.of(items, page, size, notifPage.getTotal());
    }

    @Transactional
    public void markRead(Long id) {
        notificationRepository.markRead(id);
    }

    @Transactional
    public void markAllRead(Long userId) {
        notificationRepository.markAllRead(userId);
    }

    public Long getUnreadCount(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Transactional
    public NotificationResponse createNotification(Long userId, String title, String content, String type) {
        DeveloperNotification notif = DeveloperNotification.builder()
                .userId(userId)
                .title(title)
                .content(content)
                .type(type != null ? type : "INFO")
                .isRead(false)
                .createTime(LocalDateTime.now())
                .build();
        DeveloperNotification saved = notificationRepository.save(notif);
        return toResponse(saved);
    }

    private NotificationResponse toResponse(DeveloperNotification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .title(n.getTitle())
                .content(n.getContent())
                .type(n.getType())
                .isRead(n.getIsRead())
                .link(n.getLink())
                .createTime(n.getCreateTime())
                .build();
    }
}