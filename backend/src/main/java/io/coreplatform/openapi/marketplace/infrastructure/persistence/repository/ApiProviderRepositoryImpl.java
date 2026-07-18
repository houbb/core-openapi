package io.coreplatform.openapi.marketplace.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.coreplatform.openapi.marketplace.domain.ApiProvider;
import io.coreplatform.openapi.marketplace.port.ApiProviderRepository;
import io.coreplatform.openapi.marketplace.infrastructure.persistence.entity.ApiProviderEntity;
import io.coreplatform.openapi.marketplace.infrastructure.persistence.mapper.ApiProviderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ApiProviderRepositoryImpl implements ApiProviderRepository {

    private final ApiProviderMapper mapper;

    @Override
    public ApiProvider save(ApiProvider provider) {
        ApiProviderEntity entity = toEntity(provider);
        if (entity.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<ApiProvider> findById(Long id) {
        ApiProviderEntity entity = mapper.selectById(id);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public IPage<ApiProvider> findPage(long page, long size, String keyword, String type, String status) {
        LambdaQueryWrapper<ApiProviderEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(ApiProviderEntity::getName, keyword);
        }
        if (StringUtils.hasText(type)) {
            wrapper.eq(ApiProviderEntity::getType, type);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(ApiProviderEntity::getStatus, status);
        }
        wrapper.orderByDesc(ApiProviderEntity::getCreateTime);
        IPage<ApiProviderEntity> entityPage = mapper.selectPage(new Page<>(page, size), wrapper);
        return entityPage.convert(this::toDomain);
    }

    @Override
    public List<ApiProvider> findAll() {
        return mapper.selectList(null).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        mapper.deleteById(id);
    }

    @Override
    public Long countByStatus(String status) {
        return mapper.selectCount(
                new LambdaQueryWrapper<ApiProviderEntity>().eq(ApiProviderEntity::getStatus, status));
    }

    @Override
    public Long countAll() {
        return mapper.selectCount(null);
    }

    private ApiProviderEntity toEntity(ApiProvider domain) {
        ApiProviderEntity entity = new ApiProviderEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setType(domain.getType());
        entity.setOwnerId(domain.getOwnerId());
        entity.setVerified(domain.getVerified());
        entity.setStatus(domain.getStatus());
        entity.setContactEmail(domain.getContactEmail());
        entity.setWebsite(domain.getWebsite());
        entity.setLogoUrl(domain.getLogoUrl());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        return entity;
    }

    private ApiProvider toDomain(ApiProviderEntity entity) {
        return ApiProvider.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .type(entity.getType())
                .ownerId(entity.getOwnerId())
                .verified(entity.getVerified())
                .status(entity.getStatus())
                .contactEmail(entity.getContactEmail())
                .website(entity.getWebsite())
                .logoUrl(entity.getLogoUrl())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}