package io.coreplatform.openapi.api.controller;

import io.coreplatform.openapi.api.request.RequestSchemaRequest;
import io.coreplatform.openapi.api.response.RequestSchemaResponse;
import io.coreplatform.openapi.application.command.CreateRequestSchemaCommand;
import io.coreplatform.openapi.application.domain.RequestSchema;
import io.coreplatform.openapi.application.service.RequestSchemaApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/openapi/definitions/{apiId}/request-schema")
@RequiredArgsConstructor
@Tag(name = "Request Schema Management", description = "API请求体Schema管理")
public class RequestSchemaController {

    private final RequestSchemaApplicationService requestSchemaApplicationService;

    @GetMapping
    @Operation(summary = "获取请求Schema")
    public RequestSchemaResponse get(@PathVariable Long apiId) {
        Optional<RequestSchema> schema = requestSchemaApplicationService.findByApiId(apiId);
        return schema.map(this::toResponse).orElse(null);
    }

    @PutMapping
    @Operation(summary = "创建或更新请求Schema")
    public RequestSchemaResponse save(@PathVariable Long apiId, @Valid @RequestBody RequestSchemaRequest request) {
        CreateRequestSchemaCommand command = new CreateRequestSchemaCommand();
        command.setApiId(apiId);
        command.setContentType(request.getContentType());
        command.setSchemaJson(request.getSchemaJson());
        command.setExampleJson(request.getExampleJson());
        command.setDescription(request.getDescription());

        RequestSchema schema = requestSchemaApplicationService.createOrUpdate(command);
        return toResponse(schema);
    }

    @DeleteMapping
    @Operation(summary = "删除请求Schema")
    public void delete(@PathVariable Long apiId) {
        requestSchemaApplicationService.deleteByApiId(apiId);
    }

    private RequestSchemaResponse toResponse(RequestSchema schema) {
        return RequestSchemaResponse.builder()
                .id(schema.getId())
                .apiId(schema.getApiId())
                .contentType(schema.getContentType())
                .schemaJson(schema.getSchemaJson())
                .exampleJson(schema.getExampleJson())
                .description(schema.getDescription())
                .createTime(schema.getCreateTime())
                .updateTime(schema.getUpdateTime())
                .build();
    }
}
