package io.coreplatform.openapi.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.api.exception.ResourceNotFoundException;
import io.coreplatform.openapi.api.request.ServiceRequest;
import io.coreplatform.openapi.api.response.ServiceResponse;
import io.coreplatform.openapi.application.command.CreateServiceCommand;
import io.coreplatform.openapi.application.domain.ApiService;
import io.coreplatform.openapi.application.service.ServiceApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/openapi/services")
@RequiredArgsConstructor
@Tag(name = "Service Management", description = "API服务注册管理")
public class ServiceController {

    private final ServiceApplicationService serviceApplicationService;

    @GetMapping
    @Operation(summary = "获取服务列表")
    public PageResult<ServiceResponse> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size,
            @RequestParam(required = false) String keyword) {
        IPage<ApiService> result = serviceApplicationService.findPage(page, size, keyword);
        return PageResult.of(
                result.getRecords().stream().map(this::toResponse).toList(),
                page, size, result.getTotal()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取服务详情")
    public ServiceResponse get(@PathVariable Long id) {
        ApiService service = serviceApplicationService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", id));
        return toResponse(service);
    }

    @PostMapping
    @Operation(summary = "创建服务")
    public ServiceResponse create(@Valid @RequestBody ServiceRequest request) {
        CreateServiceCommand command = new CreateServiceCommand();
        command.setServiceName(request.getServiceName());
        command.setServiceCode(request.getServiceCode());
        command.setDescription(request.getDescription());
        command.setOwner(request.getOwner());
        command.setVersion(request.getVersion());

        ApiService service = serviceApplicationService.createService(command);
        return toResponse(service);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新服务")
    public ServiceResponse update(@PathVariable Long id, @Valid @RequestBody ServiceRequest request) {
        CreateServiceCommand command = new CreateServiceCommand();
        command.setServiceName(request.getServiceName());
        command.setServiceCode(request.getServiceCode());
        command.setDescription(request.getDescription());
        command.setOwner(request.getOwner());
        command.setVersion(request.getVersion());

        ApiService service = serviceApplicationService.updateService(id, command);
        return toResponse(service);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除服务")
    public void delete(@PathVariable Long id) {
        serviceApplicationService.deleteService(id);
    }

    private ServiceResponse toResponse(ApiService service) {
        return ServiceResponse.builder()
                .id(service.getId())
                .serviceName(service.getServiceName())
                .serviceCode(service.getServiceCode())
                .description(service.getDescription())
                .owner(service.getOwner())
                .status(service.getStatus())
                .version(service.getVersion())
                .createTime(service.getCreateTime())
                .updateTime(service.getUpdateTime())
                .createUser(service.getCreateUser())
                .updateUser(service.getUpdateUser())
                .build();
    }
}
