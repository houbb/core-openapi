package io.coreplatform.openapi.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.api.exception.ResourceNotFoundException;
import io.coreplatform.openapi.api.request.RouteRequest;
import io.coreplatform.openapi.api.response.RouteResponse;
import io.coreplatform.openapi.application.command.CreateRouteCommand;
import io.coreplatform.openapi.application.domain.Route;
import io.coreplatform.openapi.application.service.RouteApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/openapi")
@RequiredArgsConstructor
@Tag(name = "Route Management", description = "网关路由管理")
public class RouteController {

    private final RouteApplicationService routeApplicationService;

    @GetMapping("/routes")
    @Operation(summary = "获取路由列表")
    public PageResult<RouteResponse> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size,
            @RequestParam(required = false) Long apiId,
            @RequestParam(required = false) String serviceName,
            @RequestParam(required = false) String status) {
        IPage<Route> result = routeApplicationService.findPage(page, size, apiId, serviceName, status);
        return PageResult.of(
                result.getRecords().stream().map(this::toResponse).toList(),
                page, size, result.getTotal()
        );
    }

    @GetMapping("/routes/{id}")
    @Operation(summary = "获取路由详情")
    public RouteResponse get(@PathVariable Long id) {
        Route route = routeApplicationService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route", id));
        return toResponse(route);
    }

    @PostMapping("/routes")
    @Operation(summary = "创建路由配置")
    public RouteResponse create(@Valid @RequestBody RouteRequest request) {
        CreateRouteCommand command = new CreateRouteCommand();
        command.setApiId(request.getApiId());
        command.setServiceName(request.getServiceName());
        command.setTargetUrl(request.getTargetUrl());
        command.setTimeout(request.getTimeout());
        command.setStatus(request.getStatus());

        Route route = routeApplicationService.createRoute(command);
        return toResponse(route);
    }

    @PutMapping("/routes/{id}")
    @Operation(summary = "更新路由配置")
    public RouteResponse update(@PathVariable Long id, @Valid @RequestBody RouteRequest request) {
        CreateRouteCommand command = new CreateRouteCommand();
        command.setApiId(request.getApiId());
        command.setServiceName(request.getServiceName());
        command.setTargetUrl(request.getTargetUrl());
        command.setTimeout(request.getTimeout());
        command.setStatus(request.getStatus());

        Route route = routeApplicationService.updateRoute(id, command);
        return toResponse(route);
    }

    @DeleteMapping("/routes/{id}")
    @Operation(summary = "删除路由配置")
    public void delete(@PathVariable Long id) {
        routeApplicationService.deleteRoute(id);
    }

    @PostMapping("/routes/{id}/activate")
    @Operation(summary = "激活路由")
    public RouteResponse activate(@PathVariable Long id) {
        return toResponse(routeApplicationService.activate(id));
    }

    @PostMapping("/routes/{id}/deactivate")
    @Operation(summary = "停用路由")
    public RouteResponse deactivate(@PathVariable Long id) {
        return toResponse(routeApplicationService.deactivate(id));
    }

    private RouteResponse toResponse(Route route) {
        return RouteResponse.builder()
                .id(route.getId())
                .apiId(route.getApiId())
                .serviceName(route.getServiceName())
                .targetUrl(route.getTargetUrl())
                .timeout(route.getTimeout())
                .status(route.getStatus())
                .createTime(route.getCreateTime())
                .updateTime(route.getUpdateTime())
                .build();
    }
}