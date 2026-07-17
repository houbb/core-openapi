package io.coreplatform.openapi.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.coreplatform.openapi.infrastructure.persistence.entity.ApiSecurityPolicyEntity;
import io.coreplatform.openapi.infrastructure.persistence.mapper.ApiSecurityPolicyMapper;
import io.coreplatform.openapi.security.domain.ApiSecurityPolicy;
import io.coreplatform.openapi.security.port.ApiSecurityPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ApiSecurityPolicyRepositoryImpl implements ApiSecurityPolicyRepository {

    private final ApiSecurityPolicyMapper apiSecurityPolicyMapper;

    @Override
    public ApiSecurityPolicy save(ApiSecurityPolicy policy) {
        ApiSecurityPolicyEntity entity = toEntity(policy);
        if (entity.getId() == null) {
            apiSecurityPolicyMapper.insert(entity);
        } else {
            apiSecurityPolicyMapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<ApiSecurityPolicy> findByApiId(Long apiId) {
        ApiSecurityPolicyEntity entity = apiSecurityPolicyMapper.selectOne(
                new LambdaQueryWrapper<ApiSecurityPolicyEntity>()
                        .eq(ApiSecurityPolicyEntity::getApiId, apiId)
        );
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public Optional<ApiSecurityPolicy> findById(Long id) {
        ApiSecurityPolicyEntity entity = apiSecurityPolicyMapper.selectById(id);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public IPage<ApiSecurityPolicy> findPage(long page, long size) {
        LambdaQueryWrapper<ApiSecurityPolicyEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(ApiSecurityPolicyEntity::getApiId);

        IPage<ApiSecurityPolicyEntity> entityPage = apiSecurityPolicyMapper.selectPage(new Page<>(page, size), wrapper);
        return entityPage.convert(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        apiSecurityPolicyMapper.deleteById(id);
    }

    private ApiSecurityPolicyEntity toEntity(ApiSecurityPolicy domain) {
        ApiSecurityPolicyEntity entity = new ApiSecurityPolicyEntity();
        entity.setId(domain.getId());
        entity.setApiId(domain.getApiId());
        entity.setRequiredPermission(domain.getRequiredPermission());
        entity.setAuthRequired(domain.getAuthRequired());
        entity.setSignRequired(domain.getSignRequired());
        entity.setIpWhiteList(domain.getIpWhiteList());
        entity.setTimeLimitEnabled(domain.getTimeLimitEnabled());
        entity.setTimeLimitStart(domain.getTimeLimitStart());
        entity.setTimeLimitEnd(domain.getTimeLimitEnd());
        entity.setTenantCheck(domain.getTenantCheck());
        entity.setStatus(domain.getStatus());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        return entity;
    }

    private ApiSecurityPolicy toDomain(ApiSecurityPolicyEntity entity) {
        return ApiSecurityPolicy.builder()
                .id(entity.getId())
                .apiId(entity.getApiId())
                .requiredPermission(entity.getRequiredPermission())
                .authRequired(entity.getAuthRequired())
                .signRequired(entity.getSignRequired())
                .ipWhiteList(entity.getIpWhiteList())
                .timeLimitEnabled(entity.getTimeLimitEnabled())
                .timeLimitStart(entity.getTimeLimitStart())
                .timeLimitEnd(entity.getTimeLimitEnd())
                .tenantCheck(entity.getTenantCheck())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}