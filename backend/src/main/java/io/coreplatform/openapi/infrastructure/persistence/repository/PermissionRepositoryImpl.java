package io.coreplatform.openapi.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.coreplatform.openapi.application.domain.Permission;
import io.coreplatform.openapi.application.port.PermissionRepository;
import io.coreplatform.openapi.infrastructure.persistence.entity.PermissionEntity;
import io.coreplatform.openapi.infrastructure.persistence.mapper.PermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PermissionRepositoryImpl implements PermissionRepository {

    private final PermissionMapper permissionMapper;

    @Override
    public Permission save(Permission permission) {
        PermissionEntity entity = toEntity(permission);
        if (entity.getId() == null) {
            permissionMapper.insert(entity);
        } else {
            permissionMapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<Permission> findById(Long id) {
        PermissionEntity entity = permissionMapper.selectById(id);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public Optional<Permission> findByName(String name) {
        PermissionEntity entity = permissionMapper.selectOne(
                new LambdaQueryWrapper<PermissionEntity>()
                        .eq(PermissionEntity::getName, name)
        );
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public IPage<Permission> findPage(long page, long size, String keyword) {
        LambdaQueryWrapper<PermissionEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(PermissionEntity::getName, keyword);
        }
        wrapper.orderByAsc(PermissionEntity::getName);

        IPage<PermissionEntity> entityPage = permissionMapper.selectPage(new Page<>(page, size), wrapper);
        return entityPage.convert(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        permissionMapper.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        Long count = permissionMapper.selectCount(
                new LambdaQueryWrapper<PermissionEntity>()
                        .eq(PermissionEntity::getName, name)
        );
        return count > 0;
    }

    // ---- Entity <-> Domain conversion ----

    private PermissionEntity toEntity(Permission domain) {
        PermissionEntity entity = new PermissionEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        return entity;
    }

    private Permission toDomain(PermissionEntity entity) {
        return Permission.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }
}