package io.coreplatform.openapi.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.coreplatform.openapi.infrastructure.persistence.entity.ApplicationRoleEntity;
import io.coreplatform.openapi.infrastructure.persistence.mapper.ApplicationRoleMapper;
import io.coreplatform.openapi.security.domain.ApplicationRole;
import io.coreplatform.openapi.security.port.ApplicationRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ApplicationRoleRepositoryImpl implements ApplicationRoleRepository {

    private final ApplicationRoleMapper applicationRoleMapper;

    @Override
    public ApplicationRole save(ApplicationRole applicationRole) {
        ApplicationRoleEntity entity = toEntity(applicationRole);
        if (entity.getId() == null) {
            applicationRoleMapper.insert(entity);
        } else {
            applicationRoleMapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public List<ApplicationRole> findByApplicationId(Long applicationId) {
        List<ApplicationRoleEntity> entities = applicationRoleMapper.selectList(
                new LambdaQueryWrapper<ApplicationRoleEntity>()
                        .eq(ApplicationRoleEntity::getApplicationId, applicationId)
        );
        return entities.stream().map(this::toDomain).toList();
    }

    @Override
    public List<ApplicationRole> findByRoleId(Long roleId) {
        List<ApplicationRoleEntity> entities = applicationRoleMapper.selectList(
                new LambdaQueryWrapper<ApplicationRoleEntity>()
                        .eq(ApplicationRoleEntity::getRoleId, roleId)
        );
        return entities.stream().map(this::toDomain).toList();
    }

    @Override
    public void delete(Long applicationId, Long roleId) {
        applicationRoleMapper.delete(
                new LambdaQueryWrapper<ApplicationRoleEntity>()
                        .eq(ApplicationRoleEntity::getApplicationId, applicationId)
                        .eq(ApplicationRoleEntity::getRoleId, roleId)
        );
    }

    @Override
    public void deleteByApplicationId(Long applicationId) {
        applicationRoleMapper.delete(
                new LambdaQueryWrapper<ApplicationRoleEntity>()
                        .eq(ApplicationRoleEntity::getApplicationId, applicationId)
        );
    }

    @Override
    public void batchSave(List<ApplicationRole> list) {
        for (ApplicationRole ar : list) {
            save(ar);
        }
    }

    private ApplicationRoleEntity toEntity(ApplicationRole domain) {
        ApplicationRoleEntity entity = new ApplicationRoleEntity();
        entity.setId(domain.getId());
        entity.setApplicationId(domain.getApplicationId());
        entity.setRoleId(domain.getRoleId());
        entity.setCreateTime(domain.getCreateTime());
        return entity;
    }

    private ApplicationRole toDomain(ApplicationRoleEntity entity) {
        return ApplicationRole.builder()
                .id(entity.getId())
                .applicationId(entity.getApplicationId())
                .roleId(entity.getRoleId())
                .createTime(entity.getCreateTime())
                .build();
    }
}