package io.coreplatform.openapi.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.coreplatform.openapi.infrastructure.persistence.entity.RolePermissionEntity;
import io.coreplatform.openapi.infrastructure.persistence.mapper.RolePermissionMapper;
import io.coreplatform.openapi.security.domain.RolePermission;
import io.coreplatform.openapi.security.port.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RolePermissionRepositoryImpl implements RolePermissionRepository {

    private final RolePermissionMapper rolePermissionMapper;

    @Override
    public RolePermission save(RolePermission rolePermission) {
        RolePermissionEntity entity = toEntity(rolePermission);
        if (entity.getId() == null) {
            rolePermissionMapper.insert(entity);
        } else {
            rolePermissionMapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public List<RolePermission> findByRoleId(Long roleId) {
        List<RolePermissionEntity> entities = rolePermissionMapper.selectList(
                new LambdaQueryWrapper<RolePermissionEntity>()
                        .eq(RolePermissionEntity::getRoleId, roleId)
        );
        return entities.stream().map(this::toDomain).toList();
    }

    @Override
    public List<RolePermission> findByPermissionId(Long permissionId) {
        List<RolePermissionEntity> entities = rolePermissionMapper.selectList(
                new LambdaQueryWrapper<RolePermissionEntity>()
                        .eq(RolePermissionEntity::getPermissionId, permissionId)
        );
        return entities.stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteByRoleId(Long roleId) {
        rolePermissionMapper.delete(
                new LambdaQueryWrapper<RolePermissionEntity>()
                        .eq(RolePermissionEntity::getRoleId, roleId)
        );
    }

    @Override
    public void batchSave(List<RolePermission> list) {
        for (RolePermission rp : list) {
            save(rp);
        }
    }

    private RolePermissionEntity toEntity(RolePermission domain) {
        RolePermissionEntity entity = new RolePermissionEntity();
        entity.setId(domain.getId());
        entity.setRoleId(domain.getRoleId());
        entity.setPermissionId(domain.getPermissionId());
        entity.setCreateTime(domain.getCreateTime());
        return entity;
    }

    private RolePermission toDomain(RolePermissionEntity entity) {
        return RolePermission.builder()
                .id(entity.getId())
                .roleId(entity.getRoleId())
                .permissionId(entity.getPermissionId())
                .createTime(entity.getCreateTime())
                .build();
    }
}