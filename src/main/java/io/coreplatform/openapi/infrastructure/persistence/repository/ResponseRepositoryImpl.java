package io.coreplatform.openapi.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.coreplatform.openapi.application.domain.ApiResponse;
import io.coreplatform.openapi.application.port.ResponseRepository;
import io.coreplatform.openapi.infrastructure.persistence.entity.ResponseEntity;
import io.coreplatform.openapi.infrastructure.persistence.mapper.ResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ResponseRepositoryImpl implements ResponseRepository {

    private final ResponseMapper responseMapper;

    @Override
    public ApiResponse save(ApiResponse response) {
        ResponseEntity entity = toEntity(response);
        if (entity.getId() == null) {
            responseMapper.insert(entity);
        } else {
            responseMapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<ApiResponse> findById(Long id) {
        ResponseEntity entity = responseMapper.selectById(id);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public List<ApiResponse> findByApiId(Long apiId) {
        List<ResponseEntity> entities = responseMapper.selectList(
                new LambdaQueryWrapper<ResponseEntity>()
                        .eq(ResponseEntity::getApiId, apiId)
        );
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        responseMapper.deleteById(id);
    }

    @Override
    public void deleteByApiId(Long apiId) {
        responseMapper.delete(new LambdaQueryWrapper<ResponseEntity>()
                .eq(ResponseEntity::getApiId, apiId));
    }

    // ---- Entity <-> Domain conversion ----

    private ResponseEntity toEntity(ApiResponse domain) {
        ResponseEntity entity = new ResponseEntity();
        entity.setId(domain.getId());
        entity.setApiId(domain.getApiId());
        entity.setStatusCode(domain.getStatusCode());
        entity.setContentType(domain.getContentType());
        entity.setSchema(domain.getSchema());
        entity.setExample(domain.getExample());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        return entity;
    }

    private ApiResponse toDomain(ResponseEntity entity) {
        return ApiResponse.builder()
                .id(entity.getId())
                .apiId(entity.getApiId())
                .statusCode(entity.getStatusCode())
                .contentType(entity.getContentType())
                .schema(entity.getSchema())
                .example(entity.getExample())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}
