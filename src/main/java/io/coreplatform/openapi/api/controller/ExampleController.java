package io.coreplatform.openapi.api.controller;

import io.coreplatform.openapi.api.request.ExampleRequest;
import io.coreplatform.openapi.api.response.ExampleResponse;
import io.coreplatform.openapi.application.command.CreateExampleCommand;
import io.coreplatform.openapi.application.domain.ApiExample;
import io.coreplatform.openapi.application.service.ExampleApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/openapi/definitions/{apiId}/examples")
@RequiredArgsConstructor
@Tag(name = "Example Management", description = "API示例管理")
public class ExampleController {

    private final ExampleApplicationService exampleApplicationService;

    @GetMapping
    @Operation(summary = "获取示例列表")
    public List<ExampleResponse> list(@PathVariable Long apiId) {
        return exampleApplicationService.findByApiId(apiId).stream()
                .map(this::toResponse).toList();
    }

    @PostMapping
    @Operation(summary = "添加示例")
    public ExampleResponse create(@PathVariable Long apiId, @Valid @RequestBody ExampleRequest request) {
        CreateExampleCommand command = new CreateExampleCommand();
        command.setApiId(apiId);
        command.setType(request.getType());
        command.setName(request.getName());
        command.setContent(request.getContent());

        ApiExample example = exampleApplicationService.createExample(command);
        return toResponse(example);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新示例")
    public ExampleResponse update(@PathVariable Long apiId, @PathVariable Long id, @Valid @RequestBody ExampleRequest request) {
        CreateExampleCommand command = new CreateExampleCommand();
        command.setApiId(apiId);
        command.setType(request.getType());
        command.setName(request.getName());
        command.setContent(request.getContent());

        ApiExample example = exampleApplicationService.updateExample(id, command);
        return toResponse(example);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除示例")
    public void delete(@PathVariable Long apiId, @PathVariable Long id) {
        exampleApplicationService.deleteExample(id);
    }

    private ExampleResponse toResponse(ApiExample example) {
        return ExampleResponse.builder()
                .id(example.getId())
                .apiId(example.getApiId())
                .type(example.getType())
                .name(example.getName())
                .content(example.getContent())
                .createTime(example.getCreateTime())
                .updateTime(example.getUpdateTime())
                .build();
    }
}
