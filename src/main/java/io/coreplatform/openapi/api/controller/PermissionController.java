package io.coreplatform.openapi.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.api.exception.ResourceNotFoundException;
import io.coreplatform.openapi.api.request.PermissionRequest;
import io.coreplatform.openapi.api.response.PermissionResponse;
import io.coreplatform.openapi.application.command.CreatePermissionCommand;
import io.coreplatform.openapi.application.domain.Permission;
import io.coreplatform.openapi.application.service.PermissionApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/openapi/permissions")
@RequiredArgsConstructor
@Tag(name = "Permission Management", description = "权限管理")
public class PermissionController {

    private final PermissionApplicationService permissionApplicationService;

    @GetMapping
    @Operation(summary = "获取权限列表")
    public PageResult<PermissionResponse> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "50") long size,
            @RequestParam(required = false) String keyword) {
        IPage<Permission> result = permissionApplicationService.findPage(page, size, keyword);
        return PageResult.of(
                result.getRecords().stream().map(this::toResponse).toList(),
                page, size, result.getTotal()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取权限详情")
    public PermissionResponse get(@PathVariable Long id) {
        Permission permission = permissionApplicationService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", id));
        return toResponse(permission);
    }

    @PostMapping
    @Operation(summary = "创建权限")
    public PermissionResponse create(@Valid @RequestBody PermissionRequest request) {
        CreatePermissionCommand command = new CreatePermissionCommand();
        command.setName(request.getName());
        command.setDescription(request.getDescription());

        Permission permission = permissionApplicationService.createPermission(command);
        return toResponse(permission);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新权限")
    public PermissionResponse update(@PathVariable Long id, @Valid @RequestBody PermissionRequest request) {
        CreatePermissionCommand command = new CreatePermissionCommand();
        command.setName(request.getName());
        command.setDescription(request.getDescription());

        Permission permission = permissionApplicationService.updatePermission(id, command);
        return toResponse(permission);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除权限")
    public void delete(@PathVariable Long id) {
        permissionApplicationService.deletePermission(id);
    }

    private PermissionResponse toResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .build();
    }
}