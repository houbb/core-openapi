package io.coreplatform.openapi.api.controller;

import io.coreplatform.openapi.application.service.DocumentationApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/openapi")
@RequiredArgsConstructor
@Tag(name = "Documentation", description = "API自动文档")
public class DocumentationController {

    private final DocumentationApplicationService documentationApplicationService;

    @GetMapping("/definitions/{apiId}/docs")
    @Operation(summary = "获取单个API的完整文档")
    public Map<String, Object> getApiDocument(@PathVariable Long apiId) {
        return documentationApplicationService.getApiDocument(apiId);
    }

    @GetMapping("/docs")
    @Operation(summary = "获取所有已发布API的文档")
    public List<Map<String, Object>> getAllDocs() {
        return documentationApplicationService.getAllPublishedDocs();
    }

    @GetMapping("/services/{serviceId}/docs")
    @Operation(summary = "获取指定服务的API文档")
    public List<Map<String, Object>> getServiceDocs(@PathVariable Long serviceId) {
        return documentationApplicationService.getServiceDocs(serviceId);
    }
}
