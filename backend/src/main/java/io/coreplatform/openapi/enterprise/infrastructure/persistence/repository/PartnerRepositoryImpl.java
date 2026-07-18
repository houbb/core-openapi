package io.coreplatform.openapi.enterprise.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.coreplatform.openapi.enterprise.domain.Partner;
import io.coreplatform.openapi.enterprise.infrastructure.persistence.entity.PartnerEntity;
import io.coreplatform.openapi.enterprise.infrastructure.persistence.mapper.PartnerMapper;
import io.coreplatform.openapi.enterprise.port.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PartnerRepositoryImpl implements PartnerRepository {

    private final PartnerMapper mapper;

    @Override
    public Partner save(Partner partner) {
        PartnerEntity entity = toEntity(partner);
        if (entity.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<Partner> findById(Long id) {
        return Optional.ofNullable(mapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public IPage<Partner> findPage(long page, long size, String keyword, String level, String status, Long organizationId) {
        LambdaQueryWrapper<PartnerEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(PartnerEntity::getName, keyword);
        }
        if (StringUtils.hasText(level)) {
            wrapper.eq(PartnerEntity::getLevel, level);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(PartnerEntity::getStatus, status);
        }
        if (organizationId != null) {
            wrapper.eq(PartnerEntity::getOrganizationId, organizationId);
        }
        wrapper.orderByDesc(PartnerEntity::getCreateTime);
        return mapper.selectPage(new Page<>(page, size), wrapper).convert(this::toDomain);
    }

    @Override
    public List<Partner> findByOrganizationId(Long organizationId) {
        LambdaQueryWrapper<PartnerEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PartnerEntity::getOrganizationId, organizationId);
        return mapper.selectList(wrapper).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        mapper.deleteById(id);
    }

    @Override
    public Long countByLevel(String level) {
        return mapper.selectCount(new LambdaQueryWrapper<PartnerEntity>().eq(PartnerEntity::getLevel, level));
    }

    @Override
    public Long countAll() {
        return mapper.selectCount(null);
    }

    private PartnerEntity toEntity(Partner domain) {
        PartnerEntity entity = new PartnerEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setName(domain.getName());
        entity.setLevel(domain.getLevel());
        entity.setStatus(domain.getStatus());
        entity.setContactName(domain.getContactName());
        entity.setContactEmail(domain.getContactEmail());
        entity.setContactPhone(domain.getContactPhone());
        entity.setDescription(domain.getDescription());
        entity.setTenantId(domain.getTenantId());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        entity.setCreateUser(domain.getCreateUser());
        entity.setUpdateUser(domain.getUpdateUser());
        return entity;
    }

    private Partner toDomain(PartnerEntity entity) {
        return Partner.builder()
                .id(entity.getId()).organizationId(entity.getOrganizationId())
                .name(entity.getName()).level(entity.getLevel()).status(entity.getStatus())
                .contactName(entity.getContactName()).contactEmail(entity.getContactEmail())
                .contactPhone(entity.getContactPhone()).description(entity.getDescription())
                .tenantId(entity.getTenantId())
                .createTime(entity.getCreateTime()).updateTime(entity.getUpdateTime())
                .createUser(entity.getCreateUser()).updateUser(entity.getUpdateUser())
                .build();
    }
}