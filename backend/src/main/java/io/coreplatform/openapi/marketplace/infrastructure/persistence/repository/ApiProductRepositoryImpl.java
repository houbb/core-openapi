package io.coreplatform.openapi.marketplace.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.coreplatform.openapi.marketplace.domain.ApiProduct;
import io.coreplatform.openapi.marketplace.port.ApiProductRepository;
import io.coreplatform.openapi.marketplace.infrastructure.persistence.entity.ApiProductEntity;
import io.coreplatform.openapi.marketplace.infrastructure.persistence.mapper.ApiProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ApiProductRepositoryImpl implements ApiProductRepository {

    private final ApiProductMapper mapper;

    @Override
    public ApiProduct save(ApiProduct product) {
        ApiProductEntity entity = toEntity(product);
        if (entity.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<ApiProduct> findById(Long id) {
        ApiProductEntity entity = mapper.selectById(id);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public Optional<ApiProduct> findByApiId(Long apiId) {
        ApiProductEntity entity = mapper.selectOne(
                new LambdaQueryWrapper<ApiProductEntity>().eq(ApiProductEntity::getApiId, apiId));
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public IPage<ApiProduct> findPage(long page, long size, String keyword, String category, String status, Long providerId) {
        LambdaQueryWrapper<ApiProductEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(ApiProductEntity::getName, keyword)
                    .or().like(ApiProductEntity::getDescription, keyword));
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(ApiProductEntity::getCategory, category);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(ApiProductEntity::getStatus, status);
        }
        if (providerId != null) {
            wrapper.eq(ApiProductEntity::getProviderId, providerId);
        }
        wrapper.orderByDesc(ApiProductEntity::getCreateTime);
        IPage<ApiProductEntity> entityPage = mapper.selectPage(new Page<>(page, size), wrapper);
        return entityPage.convert(this::toDomain);
    }

    @Override
    public List<ApiProduct> findByProviderId(Long providerId) {
        List<ApiProductEntity> entities = mapper.selectList(
                new LambdaQueryWrapper<ApiProductEntity>()
                        .eq(ApiProductEntity::getProviderId, providerId)
                        .orderByDesc(ApiProductEntity::getCreateTime));
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<ApiProduct> findPublished(String category, int limit) {
        LambdaQueryWrapper<ApiProductEntity> wrapper = new LambdaQueryWrapper<ApiProductEntity>()
                .eq(ApiProductEntity::getStatus, "PUBLISHED");
        if (StringUtils.hasText(category)) {
            wrapper.eq(ApiProductEntity::getCategory, category);
        }
        wrapper.orderByDesc(ApiProductEntity::getCreateTime);
        if (limit > 0) {
            wrapper.last("LIMIT " + limit);
        }
        return mapper.selectList(wrapper).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        mapper.deleteById(id);
    }

    @Override
    public Long countByStatus(String status) {
        return mapper.selectCount(
                new LambdaQueryWrapper<ApiProductEntity>().eq(ApiProductEntity::getStatus, status));
    }

    @Override
    public Long countAll() {
        return mapper.selectCount(null);
    }

    private ApiProductEntity toEntity(ApiProduct domain) {
        ApiProductEntity entity = new ApiProductEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setProviderId(domain.getProviderId());
        entity.setApiId(domain.getApiId());
        entity.setCategory(domain.getCategory());
        entity.setIconUrl(domain.getIconUrl());
        entity.setStatus(domain.getStatus());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        return entity;
    }

    private ApiProduct toDomain(ApiProductEntity entity) {
        return ApiProduct.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .providerId(entity.getProviderId())
                .apiId(entity.getApiId())
                .category(entity.getCategory())
                .iconUrl(entity.getIconUrl())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}