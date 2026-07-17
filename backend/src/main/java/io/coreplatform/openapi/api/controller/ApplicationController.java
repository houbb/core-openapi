package io.coreplatform.openapi.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.api.exception.ResourceNotFoundException;
import io.coreplatform.openapi.api.request.ApplicationRequest;
import io.coreplatform.openapi.api.request.GrantPermissionRequest;
import io.coreplatform.openapi.api.response.ApiKeyResponse;
import io.coreplatform.openapi.api.response.ApplicationResponse;
import io.coreplatform.openapi.api.response.PermissionResponse;
import io.coreplatform.openapi.application.command.CreateApplicationCommand;
import io.coreplatform.openapi.application.domain.Application;
import io.coreplatform.openapi.application.domain.ApplicationPermission;
import io.coreplatform.openapi.application.domain.ApiKey;
import io.coreplatform.openapi.application.domain.Permission;
import io.coreplatform.openapi.application.port.ApplicationPermissionRepository;
import io.coreplatform.openapi.application.port.PermissionRepository;
import io.coreplatform.openapi.application.service.ApiKeyApplicationService;
import io.coreplatform.openapi.application.service.ApplicationApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/openapi/applications")
@RequiredArgsConstructor
@Tag(name = "Application Management", description = "应用管理")
public class ApplicationController {

    private final ApplicationApplicationService applicationApplicationService;
    private final ApiKeyApplicationService apiKeyApplicationService;
    private final PermissionRepository permissionRepository;
    private final ApplicationPermissionRepository applicationPermissionRepository;

    // ---- Application CRUD ----

    @GetMapping
    @Operation(summary = "获取应用列表")
    public PageResult<ApplicationResponse> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size,
            @RequestParam(required = false) String keyword) {
        IPage<Application> result = applicationApplicationService.findPage(page, size, keyword);
        return PageResult.of(
                result.getRecords().stream().map(this::toResponse).toList(),
                page, size, result.getTotal()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取应用详情")
    public ApplicationResponse get(@PathVariable Long id) {
        Application app = applicationApplicationService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application", id));
        return toResponse(app);
    }

    @PostMapping
    @Operation(summary = "创建应用")
    public ApplicationResponse create(@Valid @RequestBody ApplicationRequest request) {
        CreateApplicationCommand command = new CreateApplicationCommand();
        command.setAppName(request.getAppName());
        command.setAppCode(request.getAppCode());
        command.setOwnerId(request.getOwnerId());
        command.setDescription(request.getDescription());
        command.setStatus(request.getStatus());

        Application app = applicationApplicationService.createApplication(command);
        return toResponse(app);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新应用")
    public ApplicationResponse update(@PathVariable Long id, @Valid @RequestBody ApplicationRequest request) {
        CreateApplicationCommand command = new CreateApplicationCommand();
        command.setAppName(request.getAppName());
        command.setAppCode(request.getAppCode());
        command.setOwnerId(request.getOwnerId());
        command.setDescription(request.getDescription());
        command.setStatus(request.getStatus());

        Application app = applicationApplicationService.updateApplication(id, command);
        return toResponse(app);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除应用")
    public void delete(@PathVariable Long id) {
        applicationApplicationService.deleteApplication(id);
    }

    // ---- API Keys under Application ----

    @GetMapping("/{id}/keys")
    @Operation(summary = "获取应用的API Key列表")
    public List<ApiKeyResponse> listKeys(@PathVariable Long id) {
        List<ApiKey> keys = apiKeyApplicationService.findByApplicationId(id);
        return keys.stream().map(this::toKeyResponse).toList();
    }

    @PostMapping("/{id}/keys")
    @Operation(summary = "为应用创建API Key")
    public ApiKeyResponse createKey(@PathVariable Long id,
                                     @Valid @RequestBody io.coreplatform.openapi.api.request.CreateApiKeyRequest request) {
        io.coreplatform.openapi.application.command.CreateApiKeyCommand command =
                new io.coreplatform.openapi.application.command.CreateApiKeyCommand();
        command.setApplicationId(id);
        command.setEnvironment(request.getEnvironment());
        command.setName(request.getName());
        command.setExpireTime(request.getExpireTime());

        ApiKey apiKey = apiKeyApplicationService.createApiKey(command);
        return toKeyResponse(apiKey);
    }

    @PostMapping("/{id}/keys/{keyId}/disable")
    @Operation(summary = "禁用API Key")
    public ApiKeyResponse disableKey(@PathVariable Long id, @PathVariable Long keyId) {
        ApiKey apiKey = apiKeyApplicationService.disableKey(keyId);
        return toKeyResponse(apiKey);
    }

    @PostMapping("/{id}/keys/{keyId}/enable")
    @Operation(summary = "启用API Key")
    public ApiKeyResponse enableKey(@PathVariable Long id, @PathVariable Long keyId) {
        ApiKey apiKey = apiKeyApplicationService.enableKey(keyId);
        return toKeyResponse(apiKey);
    }

    @DeleteMapping("/{id}/keys/{keyId}")
    @Operation(summary = "删除API Key")
    public void deleteKey(@PathVariable Long id, @PathVariable Long keyId) {
        apiKeyApplicationService.deleteKey(keyId);
    }

    // ---- Permissions under Application ----

    @GetMapping("/{id}/permissions")
    @Operation(summary = "获取应用的权限列表")
    public List<PermissionResponse> listPermissions(@PathVariable Long id) {
        List<ApplicationPermission> appPerms = applicationPermissionRepository.findByApplicationId(id);
        return appPerms.stream()
                .map(ap -> permissionRepository.findById(ap.getPermissionId()))
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .map(this::toPermissionResponse)
                .toList();
    }

    @PostMapping("/{id}/permissions")
    @Operation(summary = "为应用授予权限")
    public List<PermissionResponse> grantPermission(@PathVariable Long id,
                                                     @Valid @RequestBody GrantPermissionRequest request) {
        Long permId = request.getPermissionId();
        if (!applicationPermissionRepository.exists(id, permId)) {
            ApplicationPermission appPerm = ApplicationPermission.builder()
                    .applicationId(id)
                    .permissionId(permId)
                    .build();
            applicationPermissionRepository.save(appPerm);
        }
        return listPermissions(id);
    }

    @DeleteMapping("/{id}/permissions/{permId}")
    @Operation(summary = "撤销应用权限")
    public List<PermissionResponse> revokePermission(@PathVariable Long id, @PathVariable Long permId) {
        applicationPermissionRepository.delete(id, permId);
        return listPermissions(id);
    }

    // ---- Converters ----

    private ApplicationResponse toResponse(Application app) {
        return ApplicationResponse.builder()
                .id(app.getId())
                .appName(app.getAppName())
                .appCode(app.getAppCode())
                .ownerId(app.getOwnerId())
                .description(app.getDescription())
                .status(app.getStatus())
                .createTime(app.getCreateTime())
                .updateTime(app.getUpdateTime())
                .build();
    }

    private ApiKeyResponse toKeyResponse(ApiKey apiKey) {
        return ApiKeyResponse.builder()
                .id(apiKey.getId())
                .applicationId(apiKey.getApplicationId())
                .keyPrefix(apiKey.getKeyPrefix())
                .name(apiKey.getName())
                .environment(apiKey.getEnvironment())
                .status(apiKey.getStatus())
                .expireTime(apiKey.getExpireTime())
                .lastUsedTime(apiKey.getLastUsedTime())
                .createdTime(apiKey.getCreatedTime())
                .rawKey(apiKey.getRawKey()) // null for existing keys
                .build();
    }

    private PermissionResponse toPermissionResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .build();
    }
}