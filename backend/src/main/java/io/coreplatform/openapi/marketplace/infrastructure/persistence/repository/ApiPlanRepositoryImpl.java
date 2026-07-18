package io.coreplatform.openapi.marketplace.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.coreplatform.openapi.marketplace.domain.ApiPlan;
import io.coreplatform.openapi.marketplace.port.ApiPlanRepository;
import io.coreplatform.openapi.marketplace.infrastructure.persistence.entity.ApiPlanEntity;
import io.coreplatform.openapi.marketplace.infrastructure.persistence.mapper.ApiPlanMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ApiPlanRepositoryImpl implements ApiPlanRepository {

    private final ApiPlanMapper mapper;

    @Override
    public ApiPlan save(ApiPlan plan) {
        ApiPlanEntity entity = toEntity(plan);
        if (entity.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<ApiPlan> findById(Long id) {
        ApiPlanEntity entity = mapper.selectById(id);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public List<ApiPlan> findByProductId(Long productId) {
        List<ApiPlanEntity> entities = mapper.selectList(
                new LambdaQueryWrapper<ApiPlanEntity>()
                        .eq(ApiPlanEntity::getProductId, productId)
                        .orderByAsc(ApiPlanEntity::getSortOrder));
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        mapper.deleteById(id);
    }

    @Override
    public Long countByProductId(Long productId) {
        return mapper.selectCount(
                new LambdaQueryWrapper<ApiPlanEntity>().eq(ApiPlanEntity::getProductId, productId));
    }

    private ApiPlanEntity toEntity(ApiPlan domain) {
        ApiPlanEntity entity = new ApiPlanEntity();
        entity.setId(domain.getId());
        entity.setProductId(domain.getProductId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setPrice(domain.getPrice());
        entity.setBillingType(domain.getBillingType());
        entity.setLimitConfig(domain.getLimitConfig());
        entity.setStatus(domain.getStatus());
        entity.setSortOrder(domain.getSortOrder());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        return entity;
    }

    private ApiPlan toDomain(ApiPlanEntity entity) {
        return ApiPlan.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .billingType(entity.getBillingType())
                .limitConfig(entity.getLimitConfig())
                .status(entity.getStatus())
                .sortOrder(entity.getSortOrder())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}