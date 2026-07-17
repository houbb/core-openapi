package io.coreplatform.openapi.portal.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.coreplatform.openapi.portal.application.domain.ApiSubscription;
import io.coreplatform.openapi.portal.application.port.ApiSubscriptionRepository;
import io.coreplatform.openapi.portal.infrastructure.persistence.entity.ApiSubscriptionEntity;
import io.coreplatform.openapi.portal.infrastructure.persistence.mapper.ApiSubscriptionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ApiSubscriptionRepositoryImpl implements ApiSubscriptionRepository {

    private final ApiSubscriptionMapper mapper;

    @Override
    public ApiSubscription save(ApiSubscription sub) {
        ApiSubscriptionEntity entity = toEntity(sub);
        if (entity.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<ApiSubscription> findById(Long id) {
        ApiSubscriptionEntity entity = mapper.selectById(id);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public Optional<ApiSubscription> findByUserAndApi(Long userId, Long apiId) {
        ApiSubscriptionEntity entity = mapper.selectOne(
                new LambdaQueryWrapper<ApiSubscriptionEntity>()
                        .eq(ApiSubscriptionEntity::getUserId, userId)
                        .eq(ApiSubscriptionEntity::getApiId, apiId)
        );
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public List<ApiSubscription> findByUserId(Long userId) {
        List<ApiSubscriptionEntity> entities = mapper.selectList(
                new LambdaQueryWrapper<ApiSubscriptionEntity>()
                        .eq(ApiSubscriptionEntity::getUserId, userId)
                        .orderByDesc(ApiSubscriptionEntity::getCreateTime)
        );
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public IPage<ApiSubscription> findPage(long page, long size, Long userId) {
        LambdaQueryWrapper<ApiSubscriptionEntity> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(ApiSubscriptionEntity::getUserId, userId);
        }
        wrapper.orderByDesc(ApiSubscriptionEntity::getCreateTime);
        IPage<ApiSubscriptionEntity> entityPage = mapper.selectPage(new Page<>(page, size), wrapper);
        return entityPage.convert(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        mapper.deleteById(id);
    }

    @Override
    public Long countByUserId(Long userId) {
        return mapper.selectCount(
                new LambdaQueryWrapper<ApiSubscriptionEntity>()
                        .eq(ApiSubscriptionEntity::getUserId, userId)
                        .eq(ApiSubscriptionEntity::getStatus, "ACTIVE")
        );
    }

    private ApiSubscriptionEntity toEntity(ApiSubscription domain) {
        ApiSubscriptionEntity entity = new ApiSubscriptionEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setApiId(domain.getApiId());
        entity.setPlan(domain.getPlan());
        entity.setStatus(domain.getStatus());
        entity.setMaxQps(domain.getMaxQps());
        entity.setMaxDaily(domain.getMaxDaily());
        entity.setSubscribedAt(domain.getSubscribedAt());
        entity.setExpiredAt(domain.getExpiredAt());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        return entity;
    }

    private ApiSubscription toDomain(ApiSubscriptionEntity entity) {
        return ApiSubscription.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .apiId(entity.getApiId())
                .plan(entity.getPlan())
                .status(entity.getStatus())
                .maxQps(entity.getMaxQps())
                .maxDaily(entity.getMaxDaily())
                .subscribedAt(entity.getSubscribedAt())
                .expiredAt(entity.getExpiredAt())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}