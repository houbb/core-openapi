package io.coreplatform.openapi.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.coreplatform.openapi.application.domain.RequestSchema;
import io.coreplatform.openapi.application.port.RequestSchemaRepository;
import io.coreplatform.openapi.infrastructure.persistence.entity.RequestSchemaEntity;
import io.coreplatform.openapi.infrastructure.persistence.mapper.RequestSchemaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RequestSchemaRepositoryImpl implements RequestSchemaRepository {

    private final RequestSchemaMapper requestSchemaMapper;

    @Override
    public RequestSchema save(RequestSchema requestSchema) {
        RequestSchemaEntity entity = toEntity(requestSchema);
        if (entity.getId() == null) {
            requestSchemaMapper.insert(entity);
        } else {
            requestSchemaMapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<RequestSchema> findById(Long id) {
        RequestSchemaEntity entity = requestSchemaMapper.selectById(id);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public Optional<RequestSchema> findByApiId(Long apiId) {
        RequestSchemaEntity entity = requestSchemaMapper.selectOne(
                new LambdaQueryWrapper<RequestSchemaEntity>()
                        .eq(RequestSchemaEntity::getApiId, apiId)
        );
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        requestSchemaMapper.deleteById(id);
    }

    @Override
    public void deleteByApiId(Long apiId) {
        requestSchemaMapper.delete(
                new LambdaQueryWrapper<RequestSchemaEntity>()
                        .eq(RequestSchemaEntity::getApiId, apiId)
        );
    }

    // ---- Entity <-> Domain conversion ----

    private RequestSchemaEntity toEntity(RequestSchema domain) {
        RequestSchemaEntity entity = new RequestSchemaEntity();
        entity.setId(domain.getId());
        entity.setApiId(domain.getApiId());
        entity.setContentType(domain.getContentType());
        entity.setSchemaJson(domain.getSchemaJson());
        entity.setExampleJson(domain.getExampleJson());
        entity.setDescription(domain.getDescription());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        return entity;
    }

    private RequestSchema toDomain(RequestSchemaEntity entity) {
        return RequestSchema.builder()
                .id(entity.getId())
                .apiId(entity.getApiId())
                .contentType(entity.getContentType())
                .schemaJson(entity.getSchemaJson())
                .exampleJson(entity.getExampleJson())
                .description(entity.getDescription())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}
