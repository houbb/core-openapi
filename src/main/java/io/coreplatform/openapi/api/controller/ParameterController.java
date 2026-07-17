package io.coreplatform.openapi.api.controller;

import io.coreplatform.openapi.api.request.ParameterRequest;
import io.coreplatform.openapi.api.response.ParameterResponse;
import io.coreplatform.openapi.application.command.CreateParameterCommand;
import io.coreplatform.openapi.application.domain.Parameter;
import io.coreplatform.openapi.application.service.ParameterApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/openapi/definitions/{apiId}/parameters")
@RequiredArgsConstructor
@Tag(name = "Parameter Management", description = "API参数管理")
public class ParameterController {

    private final ParameterApplicationService parameterApplicationService;

    @GetMapping
    @Operation(summary = "获取接口参数列表")
    public List<ParameterResponse> list(@PathVariable Long apiId) {
        return parameterApplicationService.findByApiId(apiId).stream()
                .map(this::toResponse).toList();
    }

    @PostMapping
    @Operation(summary = "添加参数")
    public ParameterResponse create(@PathVariable Long apiId, @Valid @RequestBody ParameterRequest request) {
        CreateParameterCommand command = new CreateParameterCommand();
        command.setApiId(apiId);
        command.setName(request.getName());
        command.setLocation(request.getLocation());
        command.setType(request.getType());
        command.setRequired(request.getRequired());
        command.setDescription(request.getDescription());
        command.setExample(request.getExample());
        command.setSortOrder(request.getSortOrder());

        Parameter parameter = parameterApplicationService.createParameter(command);
        return toResponse(parameter);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新参数")
    public ParameterResponse update(@PathVariable Long apiId, @PathVariable Long id, @Valid @RequestBody ParameterRequest request) {
        CreateParameterCommand command = new CreateParameterCommand();
        command.setApiId(apiId);
        command.setName(request.getName());
        command.setLocation(request.getLocation());
        command.setType(request.getType());
        command.setRequired(request.getRequired());
        command.setDescription(request.getDescription());
        command.setExample(request.getExample());
        command.setSortOrder(request.getSortOrder());

        Parameter parameter = parameterApplicationService.updateParameter(id, command);
        return toResponse(parameter);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除参数")
    public void delete(@PathVariable Long apiId, @PathVariable Long id) {
        parameterApplicationService.deleteParameter(id);
    }

    private ParameterResponse toResponse(Parameter param) {
        return ParameterResponse.builder()
                .id(param.getId())
                .apiId(param.getApiId())
                .name(param.getName())
                .location(param.getLocation())
                .type(param.getType())
                .required(param.getRequired())
                .description(param.getDescription())
                .example(param.getExample())
                .sortOrder(param.getSortOrder())
                .createTime(param.getCreateTime())
                .updateTime(param.getUpdateTime())
                .build();
    }
}
