package io.coreplatform.openapi.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.coreplatform.openapi.application.domain.ApiExample;
import io.coreplatform.openapi.application.port.ExampleRepository;
import io.coreplatform.openapi.infrastructure.persistence.entity.ExampleEntity;
import io.coreplatform.openapi.infrastructure.persistence.mapper.ExampleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ExampleRepositoryImpl implements ExampleRepository {

    private final ExampleMapper exampleMapper;

    @Override
    public ApiExample save(ApiExample example) {
        ExampleEntity entity = toEntity(example);
        if (entity.getId() == null) {
            exampleMapper.insert(entity);
        } else {
            exampleMapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<ApiExample> findById(Long id) {
        ExampleEntity entity = exampleMapper.selectById(id);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public List<ApiExample> findByApiId(Long apiId) {
        List<ExampleEntity> entities = exampleMapper.selectList(
                new LambdaQueryWrapper<ExampleEntity>()
                        .eq(ExampleEntity::getApiId, apiId)
        );
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        exampleMapper.deleteById(id);
    }

    @Override
    public void deleteByApiId(Long apiId) {
        exampleMapper.delete(
                new LambdaQueryWrapper<ExampleEntity>()
                        .eq(ExampleEntity::getApiId, apiId)
        );
    }

    // ---- Entity <-> Domain conversion ----

    private ExampleEntity toEntity(ApiExample domain) {
        ExampleEntity entity = new ExampleEntity();
        entity.setId(domain.getId());
        entity.setApiId(domain.getApiId());
        entity.setType(domain.getType());
        entity.setName(domain.getName());
        entity.setContent(domain.getContent());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        return entity;
    }

    private ApiExample toDomain(ExampleEntity entity) {
        return ApiExample.builder()
                .id(entity.getId())
                .apiId(entity.getApiId())
                .type(entity.getType())
                .name(entity.getName())
                .content(entity.getContent())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}
