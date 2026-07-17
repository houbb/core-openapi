package io.coreplatform.openapi.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.application.command.CreateRouteCommand;
import io.coreplatform.openapi.application.domain.Route;
import io.coreplatform.openapi.application.port.DefinitionRepository;
import io.coreplatform.openapi.application.port.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RouteApplicationService {

    private final RouteRepository routeRepository;
    private final DefinitionRepository definitionRepository;

    @Transactional
    public Route createRoute(CreateRouteCommand command) {
        // Verify the API definition exists and is published
        definitionRepository.findById(command.getApiId())
                .orElseThrow(() -> new IllegalArgumentException("API接口定义不存在: " + command.getApiId()));

        // Check no duplicate route for same api_id
        routeRepository.findByApiId(command.getApiId()).ifPresent(existing -> {
            throw new IllegalStateException("该API接口已配置路由: apiId=" + command.getApiId());
        });

        Route route = Route.builder()
                .apiId(command.getApiId())
                .serviceName(command.getServiceName())
                .targetUrl(command.getTargetUrl())
                .timeout(command.getTimeout() != null ? command.getTimeout() : 30000)
                .status(command.getStatus() != null ? command.getStatus() : "ACTIVE")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        return routeRepository.save(route);
    }

    @Transactional
    public Route updateRoute(Long id, CreateRouteCommand command) {
        Route existing = routeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("路由配置不存在: " + id));

        // Verify the API definition exists
        definitionRepository.findById(command.getApiId())
                .orElseThrow(() -> new IllegalArgumentException("API接口定义不存在: " + command.getApiId()));

        existing.setApiId(command.getApiId());
        existing.setServiceName(command.getServiceName());
        existing.setTargetUrl(command.getTargetUrl());
        existing.setTimeout(command.getTimeout() != null ? command.getTimeout() : 30000);
        existing.setStatus(command.getStatus() != null ? command.getStatus() : "ACTIVE");
        existing.setUpdateTime(LocalDateTime.now());
        return routeRepository.save(existing);
    }

    public Optional<Route> findById(Long id) {
        return routeRepository.findById(id);
    }

    public IPage<Route> findPage(long page, long size, Long apiId, String serviceName, String status) {
        return routeRepository.findPage(page, size, apiId, serviceName, status);
    }

    @Transactional
    public void deleteRoute(Long id) {
        routeRepository.deleteById(id);
    }

    @Transactional
    public Route activate(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("路由配置不存在: " + id));
        route.setStatus("ACTIVE");
        route.setUpdateTime(LocalDateTime.now());
        return routeRepository.save(route);
    }

    @Transactional
    public Route deactivate(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("路由配置不存在: " + id));
        route.setStatus("INACTIVE");
        route.setUpdateTime(LocalDateTime.now());
        return routeRepository.save(route);
    }
}