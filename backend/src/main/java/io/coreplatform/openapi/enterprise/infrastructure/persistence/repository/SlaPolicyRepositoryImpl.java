package io.coreplatform.openapi.enterprise.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.coreplatform.openapi.enterprise.domain.SlaPolicy;
import io.coreplatform.openapi.enterprise.infrastructure.persistence.entity.SlaPolicyEntity;
import io.coreplatform.openapi.enterprise.infrastructure.persistence.mapper.SlaPolicyMapper;
import io.coreplatform.openapi.enterprise.port.SlaPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SlaPolicyRepositoryImpl implements SlaPolicyRepository {

    private final SlaPolicyMapper mapper;

    @Override
    public SlaPolicy save(SlaPolicy policy) {
        SlaPolicyEntity entity = toEntity(policy);
        if (entity.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<SlaPolicy> findById(Long id) {
        return Optional.ofNullable(mapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public Optional<SlaPolicy> findByOrganizationId(Long organizationId) {
        LambdaQueryWrapper<SlaPolicyEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SlaPolicyEntity::getOrganizationId, organizationId);
        return Optional.ofNullable(mapper.selectOne(wrapper)).map(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        mapper.deleteById(id);
    }

    private SlaPolicyEntity toEntity(SlaPolicy domain) {
        SlaPolicyEntity entity = new SlaPolicyEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setName(domain.getName());
        entity.setAvailability(domain.getAvailability());
        entity.setResponseTimeMs(domain.getResponseTimeMs());
        entity.setLatencyP99Ms(domain.getLatencyP99Ms());
        entity.setSupportLevel(domain.getSupportLevel());
        entity.setIncidentResponseMin(domain.getIncidentResponseMin());
        entity.setStatus(domain.getStatus());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        entity.setCreateUser(domain.getCreateUser());
        entity.setUpdateUser(domain.getUpdateUser());
        return entity;
    }

    private SlaPolicy toDomain(SlaPolicyEntity entity) {
        return SlaPolicy.builder()
                .id(entity.getId()).organizationId(entity.getOrganizationId())
                .name(entity.getName()).availability(entity.getAvailability())
                .responseTimeMs(entity.getResponseTimeMs()).latencyP99Ms(entity.getLatencyP99Ms())
                .supportLevel(entity.getSupportLevel()).incidentResponseMin(entity.getIncidentResponseMin())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime()).updateTime(entity.getUpdateTime())
                .createUser(entity.getCreateUser()).updateUser(entity.getUpdateUser())
                .build();
    }
}