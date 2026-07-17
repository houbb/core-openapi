package io.coreplatform.openapi.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.coreplatform.openapi.application.domain.ApiVersion;
import io.coreplatform.openapi.application.port.VersionRepository;
import io.coreplatform.openapi.infrastructure.persistence.entity.VersionEntity;
import io.coreplatform.openapi.infrastructure.persistence.mapper.VersionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VersionRepositoryImpl implements VersionRepository {

    private final VersionMapper versionMapper;

    @Override
    public ApiVersion save(ApiVersion version) {
        VersionEntity entity = toEntity(version);
        if (entity.getId() == null) {
            versionMapper.insert(entity);
        } else {
            versionMapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<ApiVersion> findById(Long id) {
        VersionEntity entity = versionMapper.selectById(id);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public List<ApiVersion> findByApiId(Long apiId) {
        List<VersionEntity> entities = versionMapper.selectList(
                new LambdaQueryWrapper<VersionEntity>()
                        .eq(VersionEntity::getApiId, apiId)
                        .orderByDesc(VersionEntity::getCreateTime)
        );
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        versionMapper.deleteById(id);
    }

    // ---- Entity <-> Domain conversion ----

    private VersionEntity toEntity(ApiVersion domain) {
        VersionEntity entity = new VersionEntity();
        entity.setId(domain.getId());
        entity.setApiId(domain.getApiId());
        entity.setVersion(domain.getVersion());
        entity.setStatus(domain.getStatus());
        entity.setChangelog(domain.getChangelog());
        entity.setReleaseTime(domain.getReleaseTime());
        entity.setDeprecatedTime(domain.getDeprecatedTime());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        return entity;
    }

    private ApiVersion toDomain(VersionEntity entity) {
        return ApiVersion.builder()
                .id(entity.getId())
                .apiId(entity.getApiId())
                .version(entity.getVersion())
                .status(entity.getStatus())
                .changelog(entity.getChangelog())
                .releaseTime(entity.getReleaseTime())
                .deprecatedTime(entity.getDeprecatedTime())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}
