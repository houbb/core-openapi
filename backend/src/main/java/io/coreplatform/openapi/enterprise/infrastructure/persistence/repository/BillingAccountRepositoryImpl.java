package io.coreplatform.openapi.enterprise.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.coreplatform.openapi.enterprise.domain.BillingAccount;
import io.coreplatform.openapi.enterprise.infrastructure.persistence.entity.BillingAccountEntity;
import io.coreplatform.openapi.enterprise.infrastructure.persistence.mapper.BillingAccountMapper;
import io.coreplatform.openapi.enterprise.port.BillingAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BillingAccountRepositoryImpl implements BillingAccountRepository {

    private final BillingAccountMapper mapper;

    @Override
    public BillingAccount save(BillingAccount account) {
        BillingAccountEntity entity = toEntity(account);
        if (entity.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<BillingAccount> findById(Long id) {
        return Optional.ofNullable(mapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public Optional<BillingAccount> findByOrganizationId(Long organizationId) {
        LambdaQueryWrapper<BillingAccountEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BillingAccountEntity::getOrganizationId, organizationId);
        return Optional.ofNullable(mapper.selectOne(wrapper)).map(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        mapper.deleteById(id);
    }

    private BillingAccountEntity toEntity(BillingAccount domain) {
        BillingAccountEntity entity = new BillingAccountEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setAccountName(domain.getAccountName());
        entity.setBalance(domain.getBalance());
        entity.setCurrency(domain.getCurrency());
        entity.setStatus(domain.getStatus());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        entity.setCreateUser(domain.getCreateUser());
        entity.setUpdateUser(domain.getUpdateUser());
        return entity;
    }

    private BillingAccount toDomain(BillingAccountEntity entity) {
        return BillingAccount.builder()
                .id(entity.getId()).organizationId(entity.getOrganizationId())
                .accountName(entity.getAccountName()).balance(entity.getBalance())
                .currency(entity.getCurrency()).status(entity.getStatus())
                .createTime(entity.getCreateTime()).updateTime(entity.getUpdateTime())
                .createUser(entity.getCreateUser()).updateUser(entity.getUpdateUser())
                .build();
    }
}