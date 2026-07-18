package io.coreplatform.openapi.enterprise.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.coreplatform.openapi.enterprise.domain.IdentityProvider;
import io.coreplatform.openapi.enterprise.infrastructure.persistence.entity.IdentityProviderEntity;
import io.coreplatform.openapi.enterprise.infrastructure.persistence.mapper.IdentityProviderMapper;
import io.coreplatform.openapi.enterprise.port.IdentityProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class IdentityProviderRepositoryImpl implements IdentityProviderRepository {

    private final IdentityProviderMapper mapper;

    @Override
    public IdentityProvider save(IdentityProvider provider) {
        IdentityProviderEntity entity = toEntity(provider);
        if (entity.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<IdentityProvider> findById(Long id) {
        return Optional.ofNullable(mapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public List<IdentityProvider> findByOrganizationId(Long organizationId) {
        LambdaQueryWrapper<IdentityProviderEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IdentityProviderEntity::getOrganizationId, organizationId);
        return mapper.selectList(wrapper).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<IdentityProvider> findDefaultByOrgId(Long organizationId) {
        LambdaQueryWrapper<IdentityProviderEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IdentityProviderEntity::getOrganizationId, organizationId)
               .eq(IdentityProviderEntity::getIsDefault, 1);
        return Optional.ofNullable(mapper.selectOne(wrapper)).map(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        mapper.deleteById(id);
    }

    private IdentityProviderEntity toEntity(IdentityProvider domain) {
        IdentityProviderEntity entity = new IdentityProviderEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setProviderType(domain.getProviderType());
        entity.setName(domain.getName());
        entity.setConfigJson(domain.getConfigJson());
        entity.setIsDefault(domain.getIsDefault());
        entity.setStatus(domain.getStatus());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        entity.setCreateUser(domain.getCreateUser());
        entity.setUpdateUser(domain.getUpdateUser());
        return entity;
    }

    private IdentityProvider toDomain(IdentityProviderEntity entity) {
        return IdentityProvider.builder()
                .id(entity.getId()).organizationId(entity.getOrganizationId())
                .providerType(entity.getProviderType()).name(entity.getName())
                .configJson(entity.getConfigJson()).isDefault(entity.getIsDefault())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime()).updateTime(entity.getUpdateTime())
                .createUser(entity.getCreateUser()).updateUser(entity.getUpdateUser())
                .build();
    }
}