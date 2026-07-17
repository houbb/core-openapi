package io.coreplatform.openapi.security.service;

import io.coreplatform.openapi.application.domain.ApplicationPermission;
import io.coreplatform.openapi.application.domain.Permission;
import io.coreplatform.openapi.application.port.ApplicationPermissionRepository;
import io.coreplatform.openapi.application.port.PermissionRepository;
import io.coreplatform.openapi.security.domain.ApplicationRole;
import io.coreplatform.openapi.security.domain.AuthenticationToken;
import io.coreplatform.openapi.security.domain.RolePermission;
import io.coreplatform.openapi.security.port.ApplicationRoleRepository;
import io.coreplatform.openapi.security.port.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizationService {

    private final ApplicationRoleRepository applicationRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final ApplicationPermissionRepository applicationPermissionRepository;

    /**
     * Check if the authenticated identity has the required permission.
     * Uses RBAC: application -> roles -> permissions.
     * Falls back to legacy flat application_permission mapping.
     *
     * @param token              the authenticated token
     * @param requiredPermission the permission code required by the API (e.g. "ai.chat")
     * @return true if authorized
     */
    public boolean authorize(AuthenticationToken token, String requiredPermission) {
        if (requiredPermission == null || requiredPermission.isBlank()) {
            return true; // No permission required — public API
        }

        // First check token's resolved permissions (from AuthenticationService)
        if (token.getPermissions() != null && token.getPermissions().contains(requiredPermission)) {
            return true;
        }

        // Wildcard admin check
        if (token.getPermissions() != null && token.getPermissions().contains("*")) {
            return true;
        }
        if (token.getPermissions() != null && token.getPermissions().contains("admin.all")) {
            return true;
        }

        // If token has applicationId, try RBAC resolution
        if (token.getApplicationId() != null) {
            Set<String> rbacPermissions = resolveRbacPermissions(token.getApplicationId());
            if (rbacPermissions.contains(requiredPermission)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Resolve permissions via RBAC: application -> roles -> permissions.
     */
    private Set<String> resolveRbacPermissions(Long applicationId) {
        Set<String> permissions = new HashSet<>();

        List<ApplicationRole> appRoles = applicationRoleRepository.findByApplicationId(applicationId);
        for (ApplicationRole appRole : appRoles) {
            List<RolePermission> rolePerms = rolePermissionRepository.findByRoleId(appRole.getRoleId());
            for (RolePermission rp : rolePerms) {
                Optional<Permission> perm = permissionRepository.findById(rp.getPermissionId());
                perm.ifPresent(p -> permissions.add(p.getName()));
            }
        }

        // Also check legacy flat permissions
        List<ApplicationPermission> legacyPerms = applicationPermissionRepository.findByApplicationId(applicationId);
        for (ApplicationPermission ap : legacyPerms) {
            Optional<Permission> perm = permissionRepository.findById(ap.getPermissionId());
            perm.ifPresent(p -> permissions.add(p.getName()));
        }

        return permissions;
    }
}