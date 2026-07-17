package io.coreplatform.openapi.portal.application.port;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.portal.application.domain.DeveloperNotification;

import java.util.Optional;

public interface DeveloperNotificationRepository {
    DeveloperNotification save(DeveloperNotification notification);
    Optional<DeveloperNotification> findById(Long id);
    IPage<DeveloperNotification> findByUserId(long page, long size, Long userId);
    Long countUnreadByUserId(Long userId);
    void markRead(Long id);
    void markAllRead(Long userId);
}