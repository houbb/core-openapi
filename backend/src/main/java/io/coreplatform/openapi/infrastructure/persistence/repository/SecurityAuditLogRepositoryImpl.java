package io.coreplatform.openapi.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.coreplatform.openapi.infrastructure.persistence.entity.SecurityAuditLogEntity;
import io.coreplatform.openapi.infrastructure.persistence.mapper.SecurityAuditLogMapper;
import io.coreplatform.openapi.security.domain.SecurityAuditLog;
import io.coreplatform.openapi.security.port.SecurityAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SecurityAuditLogRepositoryImpl implements SecurityAuditLogRepository {

    private final SecurityAuditLogMapper securityAuditLogMapper;

    @Override
    public SecurityAuditLog save(SecurityAuditLog log) {
        SecurityAuditLogEntity entity = toEntity(log);
        securityAuditLogMapper.insert(entity);
        return toDomain(entity);
    }

    @Override
    public IPage<SecurityAuditLog> findPage(long page, long size, String eventType, String identityId,
                                            LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<SecurityAuditLogEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(eventType)) {
            wrapper.eq(SecurityAuditLogEntity::getEventType, eventType);
        }
        if (StringUtils.hasText(identityId)) {
            wrapper.eq(SecurityAuditLogEntity::getIdentityId, identityId);
        }
        if (startTime != null) {
            wrapper.ge(SecurityAuditLogEntity::getCreatedTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(SecurityAuditLogEntity::getCreatedTime, endTime);
        }
        wrapper.orderByDesc(SecurityAuditLogEntity::getCreatedTime);

        IPage<SecurityAuditLogEntity> entityPage = securityAuditLogMapper.selectPage(new Page<>(page, size), wrapper);
        return entityPage.convert(this::toDomain);
    }

    @Override
    public long countByEventType(String eventType, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<SecurityAuditLogEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SecurityAuditLogEntity::getEventType, eventType);
        if (startTime != null) {
            wrapper.ge(SecurityAuditLogEntity::getCreatedTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(SecurityAuditLogEntity::getCreatedTime, endTime);
        }
        return securityAuditLogMapper.selectCount(wrapper);
    }

    private SecurityAuditLogEntity toEntity(SecurityAuditLog domain) {
        SecurityAuditLogEntity entity = new SecurityAuditLogEntity();
        entity.setId(domain.getId());
        entity.setEventType(domain.getEventType());
        entity.setIdentityType(domain.getIdentityType());
        entity.setIdentityId(domain.getIdentityId());
        entity.setResourceType(domain.getResourceType());
        entity.setResourceId(domain.getResourceId());
        entity.setResult(domain.getResult());
        entity.setDetail(domain.getDetail());
        entity.setIp(domain.getIp());
        entity.setRequestId(domain.getRequestId());
        entity.setTenantId(domain.getTenantId());
        entity.setCreatedTime(domain.getCreatedTime());
        return entity;
    }

    private SecurityAuditLog toDomain(SecurityAuditLogEntity entity) {
        return SecurityAuditLog.builder()
                .id(entity.getId())
                .eventType(entity.getEventType())
                .identityType(entity.getIdentityType())
                .identityId(entity.getIdentityId())
                .resourceType(entity.getResourceType())
                .resourceId(entity.getResourceId())
                .result(entity.getResult())
                .detail(entity.getDetail())
                .ip(entity.getIp())
                .requestId(entity.getRequestId())
                .tenantId(entity.getTenantId())
                .createdTime(entity.getCreatedTime())
                .build();
    }
}