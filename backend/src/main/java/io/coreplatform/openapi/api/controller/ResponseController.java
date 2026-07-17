package io.coreplatform.openapi.api.controller;

import io.coreplatform.openapi.api.request.ResponseRequest;
import io.coreplatform.openapi.api.response.ResponseItemResponse;
import io.coreplatform.openapi.application.command.CreateResponseCommand;
import io.coreplatform.openapi.application.domain.ApiResponse;
import io.coreplatform.openapi.application.service.ResponseApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/openapi/definitions/{apiId}/responses")
@RequiredArgsConstructor
@Tag(name = "Response Management", description = "API响应管理")
public class ResponseController {

    private final ResponseApplicationService responseApplicationService;

    @GetMapping
    @Operation(summary = "获取接口响应列表")
    public List<ResponseItemResponse> list(@PathVariable Long apiId) {
        return responseApplicationService.findByApiId(apiId).stream()
                .map(this::toResponse).toList();
    }

    @PostMapping
    @Operation(summary = "添加响应")
    public ResponseItemResponse create(@PathVariable Long apiId, @Valid @RequestBody ResponseRequest request) {
        CreateResponseCommand command = new CreateResponseCommand();
        command.setApiId(apiId);
        command.setStatusCode(request.getStatusCode());
        command.setContentType(request.getContentType());
        command.setSchema(request.getSchema());
        command.setExample(request.getExample());
        command.setDescription(request.getDescription());

        ApiResponse response = responseApplicationService.createResponse(command);
        return toResponse(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新响应")
    public ResponseItemResponse update(@PathVariable Long apiId, @PathVariable Long id, @Valid @RequestBody ResponseRequest request) {
        CreateResponseCommand command = new CreateResponseCommand();
        command.setApiId(apiId);
        command.setStatusCode(request.getStatusCode());
        command.setContentType(request.getContentType());
        command.setSchema(request.getSchema());
        command.setExample(request.getExample());

        ApiResponse response = responseApplicationService.updateResponse(id, command);
        return toResponse(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除响应")
    public void delete(@PathVariable Long apiId, @PathVariable Long id) {
        responseApplicationService.deleteResponse(id);
    }

    private ResponseItemResponse toResponse(ApiResponse response) {
        return ResponseItemResponse.builder()
                .id(response.getId())
                .apiId(response.getApiId())
                .statusCode(response.getStatusCode())
                .contentType(response.getContentType())
                .schema(response.getSchema())
                .example(response.getExample())
                .description(response.getDescription())
                .createTime(response.getCreateTime())
                .updateTime(response.getUpdateTime())
                .build();
    }
}