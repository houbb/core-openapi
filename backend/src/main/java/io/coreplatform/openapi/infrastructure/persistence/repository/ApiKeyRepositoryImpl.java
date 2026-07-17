package io.coreplatform.openapi.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.coreplatform.openapi.application.domain.ApiKey;
import io.coreplatform.openapi.application.port.ApiKeyRepository;
import io.coreplatform.openapi.infrastructure.persistence.entity.ApiKeyEntity;
import io.coreplatform.openapi.infrastructure.persistence.mapper.ApiKeyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ApiKeyRepositoryImpl implements ApiKeyRepository {

    private final ApiKeyMapper apiKeyMapper;

    @Override
    public ApiKey save(ApiKey apiKey) {
        ApiKeyEntity entity = toEntity(apiKey);
        if (entity.getId() == null) {
            apiKeyMapper.insert(entity);
        } else {
            apiKeyMapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<ApiKey> findById(Long id) {
        ApiKeyEntity entity = apiKeyMapper.selectById(id);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public Optional<ApiKey> findByKeyHash(String keyHash) {
        ApiKeyEntity entity = apiKeyMapper.selectOne(
                new LambdaQueryWrapper<ApiKeyEntity>()
                        .eq(ApiKeyEntity::getKeyHash, keyHash)
        );
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public List<ApiKey> findByApplicationId(Long applicationId) {
        LambdaQueryWrapper<ApiKeyEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApiKeyEntity::getApplicationId, applicationId)
                .orderByDesc(ApiKeyEntity::getCreatedTime);
        return apiKeyMapper.selectList(wrapper).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        apiKeyMapper.deleteById(id);
    }

    @Override
    public Long countByStatus(String status) {
        return apiKeyMapper.selectCount(
                new LambdaQueryWrapper<ApiKeyEntity>()
                        .eq(ApiKeyEntity::getStatus, status)
        );
    }

    // ---- Entity <-> Domain conversion ----

    private ApiKeyEntity toEntity(ApiKey domain) {
        ApiKeyEntity entity = new ApiKeyEntity();
        entity.setId(domain.getId());
        entity.setApplicationId(domain.getApplicationId());
        entity.setKeyPrefix(domain.getKeyPrefix());
        entity.setKeyHash(domain.getKeyHash());
        entity.setName(domain.getName());
        entity.setEnvironment(domain.getEnvironment());
        entity.setStatus(domain.getStatus());
        entity.setExpireTime(domain.getExpireTime());
        entity.setLastUsedTime(domain.getLastUsedTime());
        entity.setCreatedTime(domain.getCreatedTime());
        return entity;
    }

    private ApiKey toDomain(ApiKeyEntity entity) {
        return ApiKey.builder()
                .id(entity.getId())
                .applicationId(entity.getApplicationId())
                .keyPrefix(entity.getKeyPrefix())
                .keyHash(entity.getKeyHash())
                .name(entity.getName())
                .environment(entity.getEnvironment())
                .status(entity.getStatus())
                .expireTime(entity.getExpireTime())
                .lastUsedTime(entity.getLastUsedTime())
                .createdTime(entity.getCreatedTime())
                .build();
    }
}