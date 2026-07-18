package io.coreplatform.openapi.enterprise.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.coreplatform.openapi.enterprise.domain.Organization;
import io.coreplatform.openapi.enterprise.infrastructure.persistence.entity.OrganizationEntity;
import io.coreplatform.openapi.enterprise.infrastructure.persistence.mapper.OrganizationMapper;
import io.coreplatform.openapi.enterprise.port.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrganizationRepositoryImpl implements OrganizationRepository {

    private final OrganizationMapper mapper;

    @Override
    public Organization save(Organization org) {
        OrganizationEntity entity = toEntity(org);
        if (entity.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<Organization> findById(Long id) {
        return Optional.ofNullable(mapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public Optional<Organization> findByCode(String code) {
        LambdaQueryWrapper<OrganizationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrganizationEntity::getCode, code);
        return Optional.ofNullable(mapper.selectOne(wrapper)).map(this::toDomain);
    }

    @Override
    public IPage<Organization> findPage(long page, long size, String keyword, String type, String status) {
        LambdaQueryWrapper<OrganizationEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(OrganizationEntity::getName, keyword);
        }
        if (StringUtils.hasText(type)) {
            wrapper.eq(OrganizationEntity::getType, type);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(OrganizationEntity::getStatus, status);
        }
        wrapper.orderByDesc(OrganizationEntity::getCreateTime);
        return mapper.selectPage(new Page<>(page, size), wrapper).convert(this::toDomain);
    }

    @Override
    public List<Organization> findByTenantId(String tenantId) {
        LambdaQueryWrapper<OrganizationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrganizationEntity::getTenantId, tenantId);
        return mapper.selectList(wrapper).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        mapper.deleteById(id);
    }

    @Override
    public Long countByStatus(String status) {
        return mapper.selectCount(new LambdaQueryWrapper<OrganizationEntity>().eq(OrganizationEntity::getStatus, status));
    }

    @Override
    public Long countAll() {
        return mapper.selectCount(null);
    }

    private OrganizationEntity toEntity(Organization domain) {
        OrganizationEntity entity = new OrganizationEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setCode(domain.getCode());
        entity.setType(domain.getType());
        entity.setOwnerId(domain.getOwnerId());
        entity.setStatus(domain.getStatus());
        entity.setDescription(domain.getDescription());
        entity.setLogoUrl(domain.getLogoUrl());
        entity.setWebsite(domain.getWebsite());
        entity.setContactEmail(domain.getContactEmail());
        entity.setContactPhone(domain.getContactPhone());
        entity.setTenantId(domain.getTenantId());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        entity.setCreateUser(domain.getCreateUser());
        entity.setUpdateUser(domain.getUpdateUser());
        return entity;
    }

    private Organization toDomain(OrganizationEntity entity) {
        return Organization.builder()
                .id(entity.getId()).name(entity.getName()).code(entity.getCode())
                .type(entity.getType()).ownerId(entity.getOwnerId()).status(entity.getStatus())
                .description(entity.getDescription()).logoUrl(entity.getLogoUrl())
                .website(entity.getWebsite()).contactEmail(entity.getContactEmail())
                .contactPhone(entity.getContactPhone()).tenantId(entity.getTenantId())
                .createTime(entity.getCreateTime()).updateTime(entity.getUpdateTime())
                .createUser(entity.getCreateUser()).updateUser(entity.getUpdateUser())
                .build();
    }
}