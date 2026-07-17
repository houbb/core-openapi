package io.coreplatform.openapi.security.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.api.exception.ResourceNotFoundException;
import io.coreplatform.openapi.api.request.RolePermissionRequest;
import io.coreplatform.openapi.api.request.RoleRequest;
import io.coreplatform.openapi.api.response.RoleResponse;
import io.coreplatform.openapi.security.domain.ApplicationRole;
import io.coreplatform.openapi.security.domain.RolePermission;
import io.coreplatform.openapi.security.domain.SecurityRole;
import io.coreplatform.openapi.security.service.ApplicationRoleApplicationService;
import io.coreplatform.openapi.security.service.RoleApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/openapi/security/roles")
@RequiredArgsConstructor
@Tag(name = "Security Role Management", description = "安全角色管理")
public class SecurityRoleController {

    private final RoleApplicationService roleApplicationService;
    private final ApplicationRoleApplicationService applicationRoleApplicationService;

    @GetMapping
    @Operation(summary = "获取角色列表")
    public PageResult<RoleResponse> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "50") long size,
            @RequestParam(required = false) String keyword) {
        IPage<SecurityRole> result = roleApplicationService.findPage(page, size, keyword);
        return PageResult.of(
                result.getRecords().stream().map(this::toResponse).toList(),
                page, size, result.getTotal()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取角色详情")
    public RoleResponse get(@PathVariable Long id) {
        SecurityRole role = roleApplicationService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
        return toResponse(role);
    }

    @PostMapping
    @Operation(summary = "创建角色")
    public RoleResponse create(@Valid @RequestBody RoleRequest request) {
        SecurityRole role = roleApplicationService.createRole(
                request.getName(), request.getDescription(), request.getTenantId());
        return toResponse(role);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新角色")
    public RoleResponse update(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
        SecurityRole role = roleApplicationService.updateRole(id, request.getName(), request.getDescription());
        return toResponse(role);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色")
    public void delete(@PathVariable Long id) {
        roleApplicationService.deleteRole(id);
    }

    @GetMapping("/{id}/permissions")
    @Operation(summary = "获取角色的权限列表")
    public List<RolePermission> getPermissions(@PathVariable Long id) {
        return roleApplicationService.getRolePermissions(id);
    }

    @PostMapping("/{id}/permissions")
    @Operation(summary = "给角色分配权限")
    public void assignPermission(@PathVariable Long id,
                                  @Valid @RequestBody RolePermissionRequest request) {
        roleApplicationService.assignPermission(id, request.getPermissionId());
    }

    @DeleteMapping("/{id}/permissions/{permissionId}")
    @Operation(summary = "移除角色的权限")
    public void removePermission(@PathVariable Long id,
                                  @PathVariable Long permissionId) {
        roleApplicationService.removePermission(id, permissionId);
    }

    @PostMapping("/{id}/applications/{applicationId}")
    @Operation(summary = "给应用分配角色")
    public void assignRoleToApplication(@PathVariable Long id,
                                         @PathVariable Long applicationId) {
        applicationRoleApplicationService.assignRole(applicationId, id);
    }

    @DeleteMapping("/{id}/applications/{applicationId}")
    @Operation(summary = "移除应用的角色")
    public void removeRoleFromApplication(@PathVariable Long id,
                                           @PathVariable Long applicationId) {
        applicationRoleApplicationService.removeRole(applicationId, id);
    }

    @GetMapping("/applications/{applicationId}")
    @Operation(summary = "获取应用的角色列表")
    public List<ApplicationRole> getApplicationRoles(@PathVariable Long applicationId) {
        return applicationRoleApplicationService.getApplicationRoles(applicationId);
    }

    private RoleResponse toResponse(SecurityRole role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .tenantId(role.getTenantId())
                .createTime(role.getCreateTime())
                .updateTime(role.getUpdateTime())
                .build();
    }
}