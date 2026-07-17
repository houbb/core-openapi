package io.coreplatform.openapi.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.api.exception.ResourceNotFoundException;
import io.coreplatform.openapi.api.request.DefinitionRequest;
import io.coreplatform.openapi.api.response.DefinitionResponse;
import io.coreplatform.openapi.application.command.CreateDefinitionCommand;
import io.coreplatform.openapi.application.domain.Definition;
import io.coreplatform.openapi.application.service.DefinitionApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/openapi")
@RequiredArgsConstructor
@Tag(name = "Definition Management", description = "API接口定义管理")
public class DefinitionController {

    private final DefinitionApplicationService definitionApplicationService;

    // -- Global definition list --

    @GetMapping("/definitions")
    @Operation(summary = "获取接口定义列表")
    public PageResult<DefinitionResponse> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size,
            @RequestParam(required = false) Long serviceId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        IPage<Definition> result = definitionApplicationService.findPage(page, size, serviceId, keyword, status);
        return PageResult.of(
                result.getRecords().stream().map(this::toResponse).toList(),
                page, size, result.getTotal()
        );
    }

    @GetMapping("/definitions/{id}")
    @Operation(summary = "获取接口定义详情")
    public DefinitionResponse get(@PathVariable Long id) {
        Definition definition = definitionApplicationService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Definition", id));
        return toResponse(definition);
    }

    // -- By service --

    @GetMapping("/services/{serviceId}/definitions")
    @Operation(summary = "获取指定服务的接口列表")
    public PageResult<DefinitionResponse> listByService(
            @PathVariable Long serviceId,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size) {
        IPage<Definition> result = definitionApplicationService.findByServiceId(serviceId, page, size);
        return PageResult.of(
                result.getRecords().stream().map(this::toResponse).toList(),
                page, size, result.getTotal()
        );
    }

    @PostMapping("/definitions")
    @Operation(summary = "创建接口定义")
    public DefinitionResponse create(@Valid @RequestBody DefinitionRequest request) {
        CreateDefinitionCommand command = new CreateDefinitionCommand();
        command.setServiceId(request.getServiceId());
        command.setName(request.getName());
        command.setPath(request.getPath());
        command.setHttpMethod(request.getHttpMethod());
        command.setDescription(request.getDescription());
        command.setCategory(request.getCategory());

        Definition definition = definitionApplicationService.createDefinition(command);
        return toResponse(definition);
    }

    @PutMapping("/definitions/{id}")
    @Operation(summary = "更新接口定义")
    public DefinitionResponse update(@PathVariable Long id, @Valid @RequestBody DefinitionRequest request) {
        CreateDefinitionCommand command = new CreateDefinitionCommand();
        command.setServiceId(request.getServiceId());
        command.setName(request.getName());
        command.setPath(request.getPath());
        command.setHttpMethod(request.getHttpMethod());
        command.setDescription(request.getDescription());
        command.setCategory(request.getCategory());

        Definition definition = definitionApplicationService.updateDefinition(id, command);
        return toResponse(definition);
    }

    @DeleteMapping("/definitions/{id}")
    @Operation(summary = "删除接口定义")
    public void delete(@PathVariable Long id) {
        definitionApplicationService.deleteDefinition(id);
    }

    // -- Lifecycle operations --

    @PostMapping("/definitions/{id}/publish")
    @Operation(summary = "发布接口")
    public DefinitionResponse publish(@PathVariable Long id) {
        return toResponse(definitionApplicationService.publish(id));
    }

    @PostMapping("/definitions/{id}/deprecate")
    @Operation(summary = "废弃接口")
    public DefinitionResponse deprecate(@PathVariable Long id) {
        return toResponse(definitionApplicationService.deprecate(id));
    }

    @PostMapping("/definitions/{id}/offline")
    @Operation(summary = "下线接口")
    public DefinitionResponse offline(@PathVariable Long id) {
        return toResponse(definitionApplicationService.offline(id));
    }

    private DefinitionResponse toResponse(Definition definition) {
        return DefinitionResponse.builder()
                .id(definition.getId())
                .serviceId(definition.getServiceId())
                .name(definition.getName())
                .path(definition.getPath())
                .httpMethod(definition.getHttpMethod())
                .description(definition.getDescription())
                .category(definition.getCategory())
                .status(definition.getStatus())
                .createTime(definition.getCreateTime())
                .updateTime(definition.getUpdateTime())
                .createUser(definition.getCreateUser())
                .updateUser(definition.getUpdateUser())
                .build();
    }
}