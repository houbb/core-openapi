package io.coreplatform.openapi.security.service;

import io.coreplatform.openapi.application.domain.Permission;
import io.coreplatform.openapi.application.port.PermissionRepository;
import io.coreplatform.openapi.security.domain.ApplicationRole;
import io.coreplatform.openapi.security.domain.RolePermission;
import io.coreplatform.openapi.security.port.ApplicationRoleRepository;
import io.coreplatform.openapi.security.port.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ApplicationRoleApplicationService {

    private final ApplicationRoleRepository applicationRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;

    @Transactional
    public void assignRole(Long applicationId, Long roleId) {
        List<ApplicationRole> existing = applicationRoleRepository.findByApplicationId(applicationId);
        boolean alreadyAssigned = existing.stream()
                .anyMatch(ar -> ar.getRoleId().equals(roleId));
        if (alreadyAssigned) {
            return;
        }
        ApplicationRole ar = ApplicationRole.builder()
                .applicationId(applicationId)
                .roleId(roleId)
                .build();
        applicationRoleRepository.save(ar);
    }

    @Transactional
    public void removeRole(Long applicationId, Long roleId) {
        applicationRoleRepository.delete(applicationId, roleId);
    }

    @Transactional
    public void setRoles(Long applicationId, List<Long> roleIds) {
        applicationRoleRepository.deleteByApplicationId(applicationId);
        for (Long roleId : roleIds) {
            ApplicationRole ar = ApplicationRole.builder()
                    .applicationId(applicationId)
                    .roleId(roleId)
                    .build();
            applicationRoleRepository.save(ar);
        }
    }

    public List<ApplicationRole> getApplicationRoles(Long applicationId) {
        return applicationRoleRepository.findByApplicationId(applicationId);
    }

    /**
     * Get all effective permissions for an application via RBAC.
     */
    public Set<String> getEffectivePermissions(Long applicationId) {
        Set<String> permissions = new HashSet<>();
        List<ApplicationRole> appRoles = applicationRoleRepository.findByApplicationId(applicationId);
        for (ApplicationRole appRole : appRoles) {
            List<RolePermission> rolePerms = rolePermissionRepository.findByRoleId(appRole.getRoleId());
            for (RolePermission rp : rolePerms) {
                Optional<Permission> perm = permissionRepository.findById(rp.getPermissionId());
                perm.ifPresent(p -> permissions.add(p.getName()));
            }
        }
        return permissions;
    }
}