package io.coreplatform.openapi.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.coreplatform.openapi.application.domain.AccessLog;
import io.coreplatform.openapi.application.port.AccessLogRepository;
import io.coreplatform.openapi.infrastructure.persistence.entity.AccessLogEntity;
import io.coreplatform.openapi.infrastructure.persistence.mapper.AccessLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class AccessLogRepositoryImpl implements AccessLogRepository {

    private final AccessLogMapper accessLogMapper;

    @Override
    public void save(AccessLog accessLog) {
        AccessLogEntity entity = toEntity(accessLog);
        accessLogMapper.insert(entity);
    }

    @Override
    public IPage<AccessLog> findPage(long page, long size, Long apiId) {
        LambdaQueryWrapper<AccessLogEntity> wrapper = new LambdaQueryWrapper<>();
        if (apiId != null) {
            wrapper.eq(AccessLogEntity::getApiId, apiId);
        }
        wrapper.orderByDesc(AccessLogEntity::getRequestTime);

        IPage<AccessLogEntity> entityPage = accessLogMapper.selectPage(new Page<>(page, size), wrapper);
        return entityPage.convert(this::toDomain);
    }

    @Override
    public Long countToday() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return accessLogMapper.selectCount(
                new LambdaQueryWrapper<AccessLogEntity>()
                        .between(AccessLogEntity::getRequestTime, startOfDay, endOfDay)
        );
    }

    // ---- Entity <-> Domain conversion ----

    private AccessLogEntity toEntity(AccessLog domain) {
        AccessLogEntity entity = new AccessLogEntity();
        entity.setId(domain.getId());
        entity.setRequestId(domain.getRequestId());
        entity.setApiId(domain.getApiId());
        entity.setClientId(domain.getClientId());
        entity.setRequestMethod(domain.getRequestMethod());
        entity.setRequestPath(domain.getRequestPath());
        entity.setTargetUrl(domain.getTargetUrl());
        entity.setRequestTime(domain.getRequestTime());
        entity.setResponseTime(domain.getResponseTime());
        entity.setStatusCode(domain.getStatusCode());
        entity.setCostTime(domain.getCostTime());
        entity.setErrorMessage(domain.getErrorMessage());
        entity.setCreateTime(domain.getCreateTime());
        return entity;
    }

    private AccessLog toDomain(AccessLogEntity entity) {
        return AccessLog.builder()
                .id(entity.getId())
                .requestId(entity.getRequestId())
                .apiId(entity.getApiId())
                .clientId(entity.getClientId())
                .requestMethod(entity.getRequestMethod())
                .requestPath(entity.getRequestPath())
                .targetUrl(entity.getTargetUrl())
                .requestTime(entity.getRequestTime())
                .responseTime(entity.getResponseTime())
                .statusCode(entity.getStatusCode())
                .costTime(entity.getCostTime())
                .errorMessage(entity.getErrorMessage())
                .createTime(entity.getCreateTime())
                .build();
    }
}