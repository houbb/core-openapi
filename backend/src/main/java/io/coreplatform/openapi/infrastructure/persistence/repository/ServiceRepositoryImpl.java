package io.coreplatform.openapi.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.coreplatform.openapi.application.domain.ApiService;
import io.coreplatform.openapi.application.port.ServiceRepository;
import io.coreplatform.openapi.infrastructure.persistence.entity.ServiceEntity;
import io.coreplatform.openapi.infrastructure.persistence.mapper.ServiceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ServiceRepositoryImpl implements ServiceRepository {

    private final ServiceMapper serviceMapper;

    @Override
    public ApiService save(ApiService service) {
        ServiceEntity entity = toEntity(service);
        if (entity.getId() == null) {
            serviceMapper.insert(entity);
        } else {
            serviceMapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<ApiService> findById(Long id) {
        ServiceEntity entity = serviceMapper.selectById(id);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public Optional<ApiService> findByServiceCode(String serviceCode) {
        ServiceEntity entity = serviceMapper.selectOne(
                new LambdaQueryWrapper<ServiceEntity>()
                        .eq(ServiceEntity::getServiceCode, serviceCode)
        );
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public IPage<ApiService> findPage(long page, long size, String keyword) {
        LambdaQueryWrapper<ServiceEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(ServiceEntity::getServiceName, keyword)
                    .or()
                    .like(ServiceEntity::getServiceCode, keyword);
        }
        wrapper.orderByDesc(ServiceEntity::getUpdateTime);

        IPage<ServiceEntity> entityPage = serviceMapper.selectPage(new Page<>(page, size), wrapper);
        return entityPage.convert(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        serviceMapper.deleteById(id);
    }

    @Override
    public boolean existsByServiceCode(String serviceCode) {
        Long count = serviceMapper.selectCount(
                new LambdaQueryWrapper<ServiceEntity>()
                        .eq(ServiceEntity::getServiceCode, serviceCode)
        );
        return count > 0;
    }

    // ---- Entity <-> Domain conversion ----

    private ServiceEntity toEntity(ApiService domain) {
        ServiceEntity entity = new ServiceEntity();
        entity.setId(domain.getId());
        entity.setServiceName(domain.getServiceName());
        entity.setServiceCode(domain.getServiceCode());
        entity.setDescription(domain.getDescription());
        entity.setOwner(domain.getOwner());
        entity.setStatus(domain.getStatus());
        entity.setVersion(domain.getVersion());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        entity.setCreateUser(domain.getCreateUser());
        entity.setUpdateUser(domain.getUpdateUser());
        return entity;
    }

    private ApiService toDomain(ServiceEntity entity) {
        return ApiService.builder()
                .id(entity.getId())
                .serviceName(entity.getServiceName())
                .serviceCode(entity.getServiceCode())
                .description(entity.getDescription())
                .owner(entity.getOwner())
                .status(entity.getStatus())
                .version(entity.getVersion())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .createUser(entity.getCreateUser())
                .updateUser(entity.getUpdateUser())
                .build();
    }
}
