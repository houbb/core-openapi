package io.coreplatform.openapi.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.coreplatform.openapi.application.domain.Parameter;
import io.coreplatform.openapi.application.port.ParameterRepository;
import io.coreplatform.openapi.infrastructure.persistence.entity.ParameterEntity;
import io.coreplatform.openapi.infrastructure.persistence.mapper.ParameterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ParameterRepositoryImpl implements ParameterRepository {

    private final ParameterMapper parameterMapper;

    @Override
    public Parameter save(Parameter parameter) {
        ParameterEntity entity = toEntity(parameter);
        if (entity.getId() == null) {
            parameterMapper.insert(entity);
        } else {
            parameterMapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<Parameter> findById(Long id) {
        ParameterEntity entity = parameterMapper.selectById(id);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public List<Parameter> findByApiId(Long apiId) {
        List<ParameterEntity> entities = parameterMapper.selectList(
                new LambdaQueryWrapper<ParameterEntity>()
                        .eq(ParameterEntity::getApiId, apiId)
                        .orderByAsc(ParameterEntity::getSortOrder)
        );
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        parameterMapper.deleteById(id);
    }

    @Override
    public void deleteByApiId(Long apiId) {
        parameterMapper.delete(new LambdaQueryWrapper<ParameterEntity>()
                .eq(ParameterEntity::getApiId, apiId));
    }

    // ---- Entity <-> Domain conversion ----

    private ParameterEntity toEntity(Parameter domain) {
        ParameterEntity entity = new ParameterEntity();
        entity.setId(domain.getId());
        entity.setApiId(domain.getApiId());
        entity.setName(domain.getName());
        entity.setLocation(domain.getLocation());
        entity.setType(domain.getType());
        entity.setRequired(domain.getRequired() != null && domain.getRequired() ? 1 : 0);
        entity.setDescription(domain.getDescription());
        entity.setExample(domain.getExample());
        entity.setSortOrder(domain.getSortOrder());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        return entity;
    }

    private Parameter toDomain(ParameterEntity entity) {
        return Parameter.builder()
                .id(entity.getId())
                .apiId(entity.getApiId())
                .name(entity.getName())
                .location(entity.getLocation())
                .type(entity.getType())
                .required(entity.getRequired() != null && entity.getRequired() == 1)
                .description(entity.getDescription())
                .example(entity.getExample())
                .sortOrder(entity.getSortOrder())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}