package io.coreplatform.openapi.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.coreplatform.openapi.application.domain.Definition;
import io.coreplatform.openapi.application.port.DefinitionRepository;
import io.coreplatform.openapi.infrastructure.persistence.entity.DefinitionEntity;
import io.coreplatform.openapi.infrastructure.persistence.mapper.DefinitionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DefinitionRepositoryImpl implements DefinitionRepository {

    private final DefinitionMapper definitionMapper;

    @Override
    public Definition save(Definition definition) {
        DefinitionEntity entity = toEntity(definition);
        if (entity.getId() == null) {
            definitionMapper.insert(entity);
        } else {
            definitionMapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<Definition> findById(Long id) {
        DefinitionEntity entity = definitionMapper.selectById(id);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public IPage<Definition> findPage(long page, long size, Long serviceId, String keyword, String status) {
        LambdaQueryWrapper<DefinitionEntity> wrapper = new LambdaQueryWrapper<>();
        if (serviceId != null) {
            wrapper.eq(DefinitionEntity::getServiceId, serviceId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(DefinitionEntity::getName, keyword)
                    .or()
                    .like(DefinitionEntity::getPath, keyword)
                    .or()
                    .like(DefinitionEntity::getDescription, keyword)
            );
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(DefinitionEntity::getStatus, status);
        }
        wrapper.orderByDesc(DefinitionEntity::getUpdateTime);

        IPage<DefinitionEntity> entityPage = definitionMapper.selectPage(new Page<>(page, size), wrapper);
        return entityPage.convert(this::toDomain);
    }

    @Override
    public IPage<Definition> findByServiceId(Long serviceId, long page, long size) {
        LambdaQueryWrapper<DefinitionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DefinitionEntity::getServiceId, serviceId)
                .orderByDesc(DefinitionEntity::getUpdateTime);

        IPage<DefinitionEntity> entityPage = definitionMapper.selectPage(new Page<>(page, size), wrapper);
        return entityPage.convert(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        definitionMapper.deleteById(id);
    }

    @Override
    public Long countByStatus(String status) {
        return definitionMapper.selectCount(
                new LambdaQueryWrapper<DefinitionEntity>()
                        .eq(DefinitionEntity::getStatus, status)
        );
    }

    // ---- Entity <-> Domain conversion ----

    private DefinitionEntity toEntity(Definition domain) {
        DefinitionEntity entity = new DefinitionEntity();
        entity.setId(domain.getId());
        entity.setServiceId(domain.getServiceId());
        entity.setName(domain.getName());
        entity.setPath(domain.getPath());
        entity.setHttpMethod(domain.getHttpMethod());
        entity.setDescription(domain.getDescription());
        entity.setCategory(domain.getCategory());
        entity.setStatus(domain.getStatus());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        entity.setCreateUser(domain.getCreateUser());
        entity.setUpdateUser(domain.getUpdateUser());
        return entity;
    }

    private Definition toDomain(DefinitionEntity entity) {
        return Definition.builder()
                .id(entity.getId())
                .serviceId(entity.getServiceId())
                .name(entity.getName())
                .path(entity.getPath())
                .httpMethod(entity.getHttpMethod())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .createUser(entity.getCreateUser())
                .updateUser(entity.getUpdateUser())
                .build();
    }
}