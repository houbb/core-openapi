package io.coreplatform.openapi.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.coreplatform.openapi.infrastructure.persistence.entity.RiskEventEntity;
import io.coreplatform.openapi.infrastructure.persistence.mapper.RiskEventMapper;
import io.coreplatform.openapi.security.domain.RiskEvent;
import io.coreplatform.openapi.security.port.RiskEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RiskEventRepositoryImpl implements RiskEventRepository {

    private final RiskEventMapper riskEventMapper;

    @Override
    public RiskEvent save(RiskEvent event) {
        RiskEventEntity entity = toEntity(event);
        riskEventMapper.insert(entity);
        return toDomain(entity);
    }

    @Override
    public IPage<RiskEvent> findPage(long page, long size, String riskType, String identityId) {
        LambdaQueryWrapper<RiskEventEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(riskType)) {
            wrapper.eq(RiskEventEntity::getRiskType, riskType);
        }
        if (StringUtils.hasText(identityId)) {
            wrapper.eq(RiskEventEntity::getIdentityId, identityId);
        }
        wrapper.orderByDesc(RiskEventEntity::getCreatedTime);

        IPage<RiskEventEntity> entityPage = riskEventMapper.selectPage(new Page<>(page, size), wrapper);
        return entityPage.convert(this::toDomain);
    }

    @Override
    public List<RiskEvent> findByIdentityId(String identityId, int limit) {
        List<RiskEventEntity> entities = riskEventMapper.selectList(
                new LambdaQueryWrapper<RiskEventEntity>()
                        .eq(RiskEventEntity::getIdentityId, identityId)
                        .orderByDesc(RiskEventEntity::getCreatedTime)
                        .last("LIMIT " + limit)
        );
        return entities.stream().map(this::toDomain).toList();
    }

    private RiskEventEntity toEntity(RiskEvent domain) {
        RiskEventEntity entity = new RiskEventEntity();
        entity.setId(domain.getId());
        entity.setIdentityId(domain.getIdentityId());
        entity.setRiskType(domain.getRiskType());
        entity.setSeverity(domain.getSeverity());
        entity.setDetail(domain.getDetail());
        entity.setRequestCount(domain.getRequestCount());
        entity.setWindowSeconds(domain.getWindowSeconds());
        entity.setThresholdCount(domain.getThresholdCount());
        entity.setTenantId(domain.getTenantId());
        entity.setCreatedTime(domain.getCreatedTime());
        return entity;
    }

    private RiskEvent toDomain(RiskEventEntity entity) {
        return RiskEvent.builder()
                .id(entity.getId())
                .identityId(entity.getIdentityId())
                .riskType(entity.getRiskType())
                .severity(entity.getSeverity())
                .detail(entity.getDetail())
                .requestCount(entity.getRequestCount())
                .windowSeconds(entity.getWindowSeconds())
                .thresholdCount(entity.getThresholdCount())
                .tenantId(entity.getTenantId())
                .createdTime(entity.getCreatedTime())
                .build();
    }
}