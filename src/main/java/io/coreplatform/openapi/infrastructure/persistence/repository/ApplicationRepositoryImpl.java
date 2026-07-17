package io.coreplatform.openapi.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.coreplatform.openapi.application.domain.Application;
import io.coreplatform.openapi.application.port.ApplicationRepository;
import io.coreplatform.openapi.infrastructure.persistence.entity.ApplicationEntity;
import io.coreplatform.openapi.infrastructure.persistence.mapper.ApplicationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ApplicationRepositoryImpl implements ApplicationRepository {

    private final ApplicationMapper applicationMapper;

    @Override
    public Application save(Application application) {
        ApplicationEntity entity = toEntity(application);
        if (entity.getId() == null) {
            applicationMapper.insert(entity);
        } else {
            applicationMapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<Application> findById(Long id) {
        ApplicationEntity entity = applicationMapper.selectById(id);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public Optional<Application> findByAppCode(String appCode) {
        ApplicationEntity entity = applicationMapper.selectOne(
                new LambdaQueryWrapper<ApplicationEntity>()
                        .eq(ApplicationEntity::getAppCode, appCode)
        );
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public IPage<Application> findPage(long page, long size, String keyword) {
        LambdaQueryWrapper<ApplicationEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(ApplicationEntity::getAppName, keyword)
                    .or()
                    .like(ApplicationEntity::getAppCode, keyword);
        }
        wrapper.orderByDesc(ApplicationEntity::getUpdateTime);

        IPage<ApplicationEntity> entityPage = applicationMapper.selectPage(new Page<>(page, size), wrapper);
        return entityPage.convert(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        applicationMapper.deleteById(id);
    }

    @Override
    public boolean existsByAppCode(String appCode) {
        Long count = applicationMapper.selectCount(
                new LambdaQueryWrapper<ApplicationEntity>()
                        .eq(ApplicationEntity::getAppCode, appCode)
        );
        return count > 0;
    }

    @Override
    public Long count() {
        return applicationMapper.selectCount(null);
    }

    // ---- Entity <-> Domain conversion ----

    private ApplicationEntity toEntity(Application domain) {
        ApplicationEntity entity = new ApplicationEntity();
        entity.setId(domain.getId());
        entity.setAppName(domain.getAppName());
        entity.setAppCode(domain.getAppCode());
        entity.setOwnerId(domain.getOwnerId());
        entity.setDescription(domain.getDescription());
        entity.setStatus(domain.getStatus());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        return entity;
    }

    private Application toDomain(ApplicationEntity entity) {
        return Application.builder()
                .id(entity.getId())
                .appName(entity.getAppName())
                .appCode(entity.getAppCode())
                .ownerId(entity.getOwnerId())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}