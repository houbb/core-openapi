package io.coreplatform.openapi.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.coreplatform.openapi.application.domain.ApplicationPermission;
import io.coreplatform.openapi.application.port.ApplicationPermissionRepository;
import io.coreplatform.openapi.infrastructure.persistence.entity.ApplicationPermissionEntity;
import io.coreplatform.openapi.infrastructure.persistence.mapper.ApplicationPermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ApplicationPermissionRepositoryImpl implements ApplicationPermissionRepository {

    private final ApplicationPermissionMapper applicationPermissionMapper;

    @Override
    public ApplicationPermission save(ApplicationPermission permission) {
        ApplicationPermissionEntity entity = toEntity(permission);
        if (entity.getId() == null) {
            applicationPermissionMapper.insert(entity);
        } else {
            applicationPermissionMapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public List<ApplicationPermission> findByApplicationId(Long applicationId) {
        LambdaQueryWrapper<ApplicationPermissionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApplicationPermissionEntity::getApplicationId, applicationId);
        return applicationPermissionMapper.selectList(wrapper).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationPermission> findByPermissionId(Long permissionId) {
        LambdaQueryWrapper<ApplicationPermissionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApplicationPermissionEntity::getPermissionId, permissionId);
        return applicationPermissionMapper.selectList(wrapper).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long applicationId, Long permissionId) {
        LambdaQueryWrapper<ApplicationPermissionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApplicationPermissionEntity::getApplicationId, applicationId)
                .eq(ApplicationPermissionEntity::getPermissionId, permissionId);
        applicationPermissionMapper.delete(wrapper);
    }

    @Override
    public boolean exists(Long applicationId, Long permissionId) {
        Long count = applicationPermissionMapper.selectCount(
                new LambdaQueryWrapper<ApplicationPermissionEntity>()
                        .eq(ApplicationPermissionEntity::getApplicationId, applicationId)
                        .eq(ApplicationPermissionEntity::getPermissionId, permissionId)
        );
        return count > 0;
    }

    @Override
    public void deleteByApplicationId(Long applicationId) {
        LambdaQueryWrapper<ApplicationPermissionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApplicationPermissionEntity::getApplicationId, applicationId);
        applicationPermissionMapper.delete(wrapper);
    }

    // ---- Entity <-> Domain conversion ----

    private ApplicationPermissionEntity toEntity(ApplicationPermission domain) {
        ApplicationPermissionEntity entity = new ApplicationPermissionEntity();
        entity.setId(domain.getId());
        entity.setApplicationId(domain.getApplicationId());
        entity.setPermissionId(domain.getPermissionId());
        return entity;
    }

    private ApplicationPermission toDomain(ApplicationPermissionEntity entity) {
        return ApplicationPermission.builder()
                .id(entity.getId())
                .applicationId(entity.getApplicationId())
                .permissionId(entity.getPermissionId())
                .build();
    }
}