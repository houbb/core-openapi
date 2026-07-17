package io.coreplatform.openapi.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.coreplatform.openapi.infrastructure.persistence.entity.SecurityRoleEntity;
import io.coreplatform.openapi.infrastructure.persistence.mapper.SecurityRoleMapper;
import io.coreplatform.openapi.security.domain.SecurityRole;
import io.coreplatform.openapi.security.port.SecurityRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityRoleRepositoryImpl implements SecurityRoleRepository {

    private final SecurityRoleMapper securityRoleMapper;

    @Override
    public SecurityRole save(SecurityRole role) {
        SecurityRoleEntity entity = toEntity(role);
        if (entity.getId() == null) {
            securityRoleMapper.insert(entity);
        } else {
            securityRoleMapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<SecurityRole> findById(Long id) {
        SecurityRoleEntity entity = securityRoleMapper.selectById(id);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public Optional<SecurityRole> findByName(String name) {
        SecurityRoleEntity entity = securityRoleMapper.selectOne(
                new LambdaQueryWrapper<SecurityRoleEntity>()
                        .eq(SecurityRoleEntity::getName, name)
        );
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public List<SecurityRole> findByTenantId(String tenantId) {
        List<SecurityRoleEntity> entities = securityRoleMapper.selectList(
                new LambdaQueryWrapper<SecurityRoleEntity>()
                        .eq(SecurityRoleEntity::getTenantId, tenantId)
                        .orderByAsc(SecurityRoleEntity::getName)
        );
        return entities.stream().map(this::toDomain).toList();
    }

    @Override
    public IPage<SecurityRole> findPage(long page, long size, String keyword) {
        LambdaQueryWrapper<SecurityRoleEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SecurityRoleEntity::getName, keyword);
        }
        wrapper.orderByAsc(SecurityRoleEntity::getName);

        IPage<SecurityRoleEntity> entityPage = securityRoleMapper.selectPage(new Page<>(page, size), wrapper);
        return entityPage.convert(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        securityRoleMapper.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        Long count = securityRoleMapper.selectCount(
                new LambdaQueryWrapper<SecurityRoleEntity>()
                        .eq(SecurityRoleEntity::getName, name)
        );
        return count > 0;
    }

    private SecurityRoleEntity toEntity(SecurityRole domain) {
        SecurityRoleEntity entity = new SecurityRoleEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setTenantId(domain.getTenantId());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        return entity;
    }

    private SecurityRole toDomain(SecurityRoleEntity entity) {
        return SecurityRole.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .tenantId(entity.getTenantId())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}