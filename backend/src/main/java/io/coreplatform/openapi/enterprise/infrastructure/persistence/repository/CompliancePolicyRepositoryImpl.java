package io.coreplatform.openapi.enterprise.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.coreplatform.openapi.enterprise.domain.CompliancePolicy;
import io.coreplatform.openapi.enterprise.infrastructure.persistence.entity.CompliancePolicyEntity;
import io.coreplatform.openapi.enterprise.infrastructure.persistence.mapper.CompliancePolicyMapper;
import io.coreplatform.openapi.enterprise.port.CompliancePolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompliancePolicyRepositoryImpl implements CompliancePolicyRepository {

    private final CompliancePolicyMapper mapper;

    @Override
    public CompliancePolicy save(CompliancePolicy policy) {
        CompliancePolicyEntity entity = toEntity(policy);
        if (entity.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<CompliancePolicy> findById(Long id) {
        return Optional.ofNullable(mapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public List<CompliancePolicy> findByOrganizationId(Long organizationId) {
        LambdaQueryWrapper<CompliancePolicyEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CompliancePolicyEntity::getOrganizationId, organizationId);
        return mapper.selectList(wrapper).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<CompliancePolicy> findByOrganizationIdAndType(Long organizationId, String policyType) {
        LambdaQueryWrapper<CompliancePolicyEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CompliancePolicyEntity::getOrganizationId, organizationId)
               .eq(CompliancePolicyEntity::getPolicyType, policyType);
        return mapper.selectList(wrapper).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        mapper.deleteById(id);
    }

    private CompliancePolicyEntity toEntity(CompliancePolicy domain) {
        CompliancePolicyEntity entity = new CompliancePolicyEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setName(domain.getName());
        entity.setPolicyType(domain.getPolicyType());
        entity.setConfigJson(domain.getConfigJson());
        entity.setStatus(domain.getStatus());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        entity.setCreateUser(domain.getCreateUser());
        entity.setUpdateUser(domain.getUpdateUser());
        return entity;
    }

    private CompliancePolicy toDomain(CompliancePolicyEntity entity) {
        return CompliancePolicy.builder()
                .id(entity.getId()).organizationId(entity.getOrganizationId())
                .name(entity.getName()).policyType(entity.getPolicyType())
                .configJson(entity.getConfigJson()).status(entity.getStatus())
                .createTime(entity.getCreateTime()).updateTime(entity.getUpdateTime())
                .createUser(entity.getCreateUser()).updateUser(entity.getUpdateUser())
                .build();
    }
}