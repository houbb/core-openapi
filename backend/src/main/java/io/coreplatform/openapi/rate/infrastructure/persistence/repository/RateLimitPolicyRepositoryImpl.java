package io.coreplatform.openapi.rate.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.coreplatform.openapi.rate.application.domain.RateLimitPolicy;
import io.coreplatform.openapi.rate.application.port.RateLimitPolicyRepository;
import io.coreplatform.openapi.rate.infrastructure.persistence.entity.RateLimitPolicyEntity;
import io.coreplatform.openapi.rate.infrastructure.persistence.mapper.RateLimitPolicyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RateLimitPolicyRepositoryImpl implements RateLimitPolicyRepository {

    private final RateLimitPolicyMapper mapper;

    @Override
    public Optional<RateLimitPolicy> findById(Long id) {
        RateLimitPolicyEntity entity = mapper.selectById(id);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public List<RateLimitPolicy> findByScopeAndScopeId(String scope, Long scopeId) {
        LambdaQueryWrapper<RateLimitPolicyEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RateLimitPolicyEntity::getScope, scope);
        if (scopeId != null) {
            wrapper.eq(RateLimitPolicyEntity::getScopeId, scopeId);
        }
        wrapper.eq(RateLimitPolicyEntity::getStatus, "ACTIVE");
        return mapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    @Override
    public List<RateLimitPolicy> findAll() {
        return mapper.selectList(null).stream().map(this::toDomain).toList();
    }

    @Override
    public List<RateLimitPolicy> findByStatus(String status) {
        LambdaQueryWrapper<RateLimitPolicyEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RateLimitPolicyEntity::getStatus, status);
        return mapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    @Override
    public RateLimitPolicy save(RateLimitPolicy policy) {
        RateLimitPolicyEntity entity = toEntity(policy);
        if (entity.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public void deleteById(Long id) {
        mapper.deleteById(id);
    }

    private RateLimitPolicy toDomain(RateLimitPolicyEntity entity) {
        return RateLimitPolicy.builder()
                .id(entity.getId())
                .name(entity.getName())
                .scope(entity.getScope())
                .scopeId(entity.getScopeId())
                .algorithm(entity.getAlgorithm())
                .limitValue(entity.getLimitValue())
                .refillRate(entity.getRefillRate())
                .refillPeriod(entity.getRefillPeriod())
                .status(entity.getStatus())
                .description(entity.getDescription())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }

    private RateLimitPolicyEntity toEntity(RateLimitPolicy domain) {
        RateLimitPolicyEntity entity = new RateLimitPolicyEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setScope(domain.getScope());
        entity.setScopeId(domain.getScopeId());
        entity.setAlgorithm(domain.getAlgorithm());
        entity.setLimitValue(domain.getLimitValue());
        entity.setRefillRate(domain.getRefillRate());
        entity.setRefillPeriod(domain.getRefillPeriod());
        entity.setStatus(domain.getStatus());
        entity.setDescription(domain.getDescription());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        return entity;
    }
}