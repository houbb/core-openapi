package io.coreplatform.openapi.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.coreplatform.openapi.application.port.SdkProjectRepository;
import io.coreplatform.openapi.infrastructure.persistence.entity.SdkProjectEntity;
import io.coreplatform.openapi.infrastructure.persistence.mapper.SdkProjectMapper;
import io.coreplatform.openapi.sdk.domain.SdkProject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SdkProjectRepositoryImpl implements SdkProjectRepository {

    private final SdkProjectMapper mapper;

    @Override
    public SdkProject save(SdkProject project) {
        SdkProjectEntity entity = toEntity(project);
        if (entity.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<SdkProject> findById(Long id) {
        return Optional.ofNullable(mapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public IPage<SdkProject> findPage(long page, long size) {
        LambdaQueryWrapper<SdkProjectEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SdkProjectEntity::getUpdateTime);
        return mapper.selectPage(new Page<>(page, size), wrapper).convert(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        mapper.deleteById(id);
    }

    private SdkProjectEntity toEntity(SdkProject domain) {
        SdkProjectEntity entity = new SdkProjectEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setLanguage(domain.getLanguage());
        entity.setVersion(domain.getVersion());
        entity.setStatus(domain.getStatus());
        entity.setErrorMessage(domain.getErrorMessage());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        return entity;
    }

    private SdkProject toDomain(SdkProjectEntity entity) {
        return SdkProject.builder()
                .id(entity.getId())
                .name(entity.getName())
                .language(entity.getLanguage())
                .version(entity.getVersion())
                .status(entity.getStatus())
                .errorMessage(entity.getErrorMessage())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}