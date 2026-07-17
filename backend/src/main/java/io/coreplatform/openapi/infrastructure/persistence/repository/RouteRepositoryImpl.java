package io.coreplatform.openapi.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.coreplatform.openapi.application.domain.Route;
import io.coreplatform.openapi.application.port.RouteRepository;
import io.coreplatform.openapi.infrastructure.persistence.entity.RouteEntity;
import io.coreplatform.openapi.infrastructure.persistence.mapper.RouteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RouteRepositoryImpl implements RouteRepository {

    private final RouteMapper routeMapper;

    @Override
    public Route save(Route route) {
        RouteEntity entity = toEntity(route);
        if (entity.getId() == null) {
            routeMapper.insert(entity);
        } else {
            routeMapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<Route> findById(Long id) {
        RouteEntity entity = routeMapper.selectById(id);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public Optional<Route> findByApiId(Long apiId) {
        LambdaQueryWrapper<RouteEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RouteEntity::getApiId, apiId)
                .eq(RouteEntity::getStatus, "ACTIVE");
        RouteEntity entity = routeMapper.selectOne(wrapper);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public List<Route> findActiveRoutes() {
        LambdaQueryWrapper<RouteEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RouteEntity::getStatus, "ACTIVE");
        return routeMapper.selectList(wrapper).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public IPage<Route> findPage(long page, long size, Long apiId, String serviceName, String status) {
        LambdaQueryWrapper<RouteEntity> wrapper = new LambdaQueryWrapper<>();
        if (apiId != null) {
            wrapper.eq(RouteEntity::getApiId, apiId);
        }
        if (StringUtils.hasText(serviceName)) {
            wrapper.like(RouteEntity::getServiceName, serviceName);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(RouteEntity::getStatus, status);
        }
        wrapper.orderByDesc(RouteEntity::getUpdateTime);

        IPage<RouteEntity> entityPage = routeMapper.selectPage(new Page<>(page, size), wrapper);
        return entityPage.convert(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        routeMapper.deleteById(id);
    }

    @Override
    public Long countByStatus(String status) {
        return routeMapper.selectCount(
                new LambdaQueryWrapper<RouteEntity>()
                        .eq(RouteEntity::getStatus, status)
        );
    }

    // ---- Entity <-> Domain conversion ----

    private RouteEntity toEntity(Route domain) {
        RouteEntity entity = new RouteEntity();
        entity.setId(domain.getId());
        entity.setApiId(domain.getApiId());
        entity.setServiceName(domain.getServiceName());
        entity.setTargetUrl(domain.getTargetUrl());
        entity.setTimeout(domain.getTimeout());
        entity.setStatus(domain.getStatus());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        return entity;
    }

    private Route toDomain(RouteEntity entity) {
        return Route.builder()
                .id(entity.getId())
                .apiId(entity.getApiId())
                .serviceName(entity.getServiceName())
                .targetUrl(entity.getTargetUrl())
                .timeout(entity.getTimeout())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}