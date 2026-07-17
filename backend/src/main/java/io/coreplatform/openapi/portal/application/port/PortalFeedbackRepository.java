package io.coreplatform.openapi.portal.application.port;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.portal.application.domain.PortalFeedback;

import java.util.Optional;

public interface PortalFeedbackRepository {
    PortalFeedback save(PortalFeedback feedback);
    Optional<PortalFeedback> findById(Long id);
    IPage<PortalFeedback> findByUserId(long page, long size, Long userId);
    IPage<PortalFeedback> findPage(long page, long size, String status);
}
