package io.coreplatform.openapi.enterprise.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.coreplatform.openapi.enterprise.domain.Contract;
import io.coreplatform.openapi.enterprise.infrastructure.persistence.entity.ContractEntity;
import io.coreplatform.openapi.enterprise.infrastructure.persistence.mapper.ContractMapper;
import io.coreplatform.openapi.enterprise.port.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ContractRepositoryImpl implements ContractRepository {

    private final ContractMapper mapper;

    @Override
    public Contract save(Contract contract) {
        ContractEntity entity = toEntity(contract);
        if (entity.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<Contract> findById(Long id) {
        return Optional.ofNullable(mapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public Optional<Contract> findByContractNo(String contractNo) {
        LambdaQueryWrapper<ContractEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ContractEntity::getContractNo, contractNo);
        return Optional.ofNullable(mapper.selectOne(wrapper)).map(this::toDomain);
    }

    @Override
    public IPage<Contract> findPage(long page, long size, Long organizationId, String status) {
        LambdaQueryWrapper<ContractEntity> wrapper = new LambdaQueryWrapper<>();
        if (organizationId != null) {
            wrapper.eq(ContractEntity::getOrganizationId, organizationId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(ContractEntity::getStatus, status);
        }
        wrapper.orderByDesc(ContractEntity::getCreateTime);
        return mapper.selectPage(new Page<>(page, size), wrapper).convert(this::toDomain);
    }

    @Override
    public List<Contract> findByOrganizationId(Long organizationId) {
        LambdaQueryWrapper<ContractEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ContractEntity::getOrganizationId, organizationId);
        return mapper.selectList(wrapper).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        mapper.deleteById(id);
    }

    @Override
    public Long countByStatus(String status) {
        return mapper.selectCount(new LambdaQueryWrapper<ContractEntity>().eq(ContractEntity::getStatus, status));
    }

    private ContractEntity toEntity(Contract domain) {
        ContractEntity entity = new ContractEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setContractNo(domain.getContractNo());
        entity.setName(domain.getName());
        entity.setPlanName(domain.getPlanName());
        entity.setStartDate(domain.getStartDate());
        entity.setEndDate(domain.getEndDate());
        entity.setStatus(domain.getStatus());
        entity.setMaxRequests(domain.getMaxRequests());
        entity.setMaxQps(domain.getMaxQps());
        entity.setSupportsPhone(domain.getSupportsPhone());
        entity.setSupports724(domain.getSupports724());
        entity.setDescription(domain.getDescription());
        entity.setTenantId(domain.getTenantId());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        entity.setCreateUser(domain.getCreateUser());
        entity.setUpdateUser(domain.getUpdateUser());
        return entity;
    }

    private Contract toDomain(ContractEntity entity) {
        return Contract.builder()
                .id(entity.getId()).organizationId(entity.getOrganizationId())
                .contractNo(entity.getContractNo()).name(entity.getName())
                .planName(entity.getPlanName()).startDate(entity.getStartDate())
                .endDate(entity.getEndDate()).status(entity.getStatus())
                .maxRequests(entity.getMaxRequests()).maxQps(entity.getMaxQps())
                .supportsPhone(entity.getSupportsPhone()).supports724(entity.getSupports724())
                .description(entity.getDescription()).tenantId(entity.getTenantId())
                .createTime(entity.getCreateTime()).updateTime(entity.getUpdateTime())
                .createUser(entity.getCreateUser()).updateUser(entity.getUpdateUser())
                .build();
    }
}