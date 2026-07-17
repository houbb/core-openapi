package io.coreplatform.openapi.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.coreplatform.openapi.application.port.SdkGenerationRepository;
import io.coreplatform.openapi.infrastructure.persistence.entity.SdkGenerationEntity;
import io.coreplatform.openapi.infrastructure.persistence.mapper.SdkGenerationMapper;
import io.coreplatform.openapi.sdk.domain.SdkGeneration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SdkGenerationRepositoryImpl implements SdkGenerationRepository {

    private final SdkGenerationMapper mapper;

    @Override
    public SdkGeneration save(SdkGeneration generation) {
        SdkGenerationEntity entity = toEntity(generation);
        if (entity.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<SdkGeneration> findById(Long id) {
        return Optional.ofNullable(mapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public IPage<SdkGeneration> findByProjectId(Long projectId, long page, long size) {
        LambdaQueryWrapper<SdkGenerationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SdkGenerationEntity::getSdkProjectId, projectId)
                .orderByDesc(SdkGenerationEntity::getCreateTime);
        return mapper.selectPage(new Page<>(page, size), wrapper).convert(this::toDomain);
    }

    @Override
    public Optional<SdkGeneration> findLatestByProjectId(Long projectId) {
        LambdaQueryWrapper<SdkGenerationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SdkGenerationEntity::getSdkProjectId, projectId)
                .orderByDesc(SdkGenerationEntity::getCreateTime)
                .last("LIMIT 1");
        return Optional.ofNullable(mapper.selectOne(wrapper)).map(this::toDomain);
    }

    private SdkGenerationEntity toEntity(SdkGeneration domain) {
        SdkGenerationEntity entity = new SdkGenerationEntity();
        entity.setId(domain.getId());
        entity.setSdkProjectId(domain.getSdkProjectId());
        entity.setApiIds(domain.getApiIds());
        entity.setApiVersion(domain.getApiVersion());
        entity.setGeneratorVersion(domain.getGeneratorVersion());
        entity.setStatus(domain.getStatus());
        entity.setDownloadUrl(domain.getDownloadUrl());
        entity.setFileSize(domain.getFileSize());
        entity.setErrorMessage(domain.getErrorMessage());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        return entity;
    }

    private SdkGeneration toDomain(SdkGenerationEntity entity) {
        return SdkGeneration.builder()
                .id(entity.getId())
                .sdkProjectId(entity.getSdkProjectId())
                .apiIds(entity.getApiIds())
                .apiVersion(entity.getApiVersion())
                .generatorVersion(entity.getGeneratorVersion())
                .status(entity.getStatus())
                .downloadUrl(entity.getDownloadUrl())
                .fileSize(entity.getFileSize())
                .errorMessage(entity.getErrorMessage())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}