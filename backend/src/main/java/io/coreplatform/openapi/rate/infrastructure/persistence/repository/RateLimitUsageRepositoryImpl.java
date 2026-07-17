package io.coreplatform.openapi.rate.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.coreplatform.openapi.rate.application.domain.RateLimitUsage;
import io.coreplatform.openapi.rate.application.port.RateLimitUsageRepository;
import io.coreplatform.openapi.rate.infrastructure.persistence.entity.RateLimitUsageEntity;
import io.coreplatform.openapi.rate.infrastructure.persistence.mapper.RateLimitUsageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RateLimitUsageRepositoryImpl implements RateLimitUsageRepository {

    private final RateLimitUsageMapper mapper;

    @Override
    public void save(RateLimitUsage usage) {
        mapper.insert(toEntity(usage));
    }

    @Override
    public List<RateLimitUsage> findByPolicyId(Long policyId) {
        LambdaQueryWrapper<RateLimitUsageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RateLimitUsageEntity::getPolicyId, policyId);
        return mapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    @Override
    public List<RateLimitUsage> findByIdentityId(String identityId) {
        LambdaQueryWrapper<RateLimitUsageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RateLimitUsageEntity::getIdentityId, identityId);
        return mapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    private RateLimitUsage toDomain(RateLimitUsageEntity entity) {
        return RateLimitUsage.builder()
                .id(entity.getId())
                .policyId(entity.getPolicyId())
                .apiId(entity.getApiId())
                .applicationId(entity.getApplicationId())
                .identityId(entity.getIdentityId())
                .requestCount(entity.getRequestCount())
                .blockedCount(entity.getBlockedCount())
                .recordedAt(entity.getRecordedAt())
                .createTime(entity.getCreateTime())
                .build();
    }

    private RateLimitUsageEntity toEntity(RateLimitUsage domain) {
        RateLimitUsageEntity entity = new RateLimitUsageEntity();
        entity.setId(domain.getId());
        entity.setPolicyId(domain.getPolicyId());
        entity.setApiId(domain.getApiId());
        entity.setApplicationId(domain.getApplicationId());
        entity.setIdentityId(domain.getIdentityId());
        entity.setRequestCount(domain.getRequestCount());
        entity.setBlockedCount(domain.getBlockedCount());
        entity.setRecordedAt(domain.getRecordedAt());
        entity.setCreateTime(domain.getCreateTime());
        return entity;
    }
}