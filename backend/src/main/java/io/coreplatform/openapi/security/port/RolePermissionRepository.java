package io.coreplatform.openapi.security.port;

import io.coreplatform.openapi.security.domain.RolePermission;

import java.util.List;

public interface RolePermissionRepository {

    RolePermission save(RolePermission rolePermission);

    List<RolePermission> findByRoleId(Long roleId);

    List<RolePermission> findByPermissionId(Long permissionId);

    void deleteByRoleId(Long roleId);

    void batchSave(List<RolePermission> list);
}