package io.coreplatform.openapi.security.port;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.security.domain.SecurityAuditLog;

import java.time.LocalDateTime;

public interface SecurityAuditLogRepository {

    SecurityAuditLog save(SecurityAuditLog log);

    IPage<SecurityAuditLog> findPage(long page, long size, String eventType, String identityId,
                                     LocalDateTime startTime, LocalDateTime endTime);

    long countByEventType(String eventType, LocalDateTime startTime, LocalDateTime endTime);
}