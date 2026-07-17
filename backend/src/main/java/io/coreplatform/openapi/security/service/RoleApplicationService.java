package io.coreplatform.openapi.security.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.security.domain.RolePermission;
import io.coreplatform.openapi.security.domain.SecurityRole;
import io.coreplatform.openapi.security.port.RolePermissionRepository;
import io.coreplatform.openapi.security.port.SecurityRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleApplicationService {

    private final SecurityRoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Transactional
    public SecurityRole createRole(String name, String description, String tenantId) {
        if (roleRepository.existsByName(name)) {
            throw new IllegalArgumentException("角色名称已存在: " + name);
        }
        SecurityRole role = SecurityRole.builder()
                .name(name)
                .description(description != null ? description : "")
                .tenantId(tenantId != null ? tenantId : "")
                .build();
        return roleRepository.save(role);
    }

    @Transactional
    public SecurityRole updateRole(Long id, String name, String description) {
        SecurityRole existing = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("角色不存在: " + id));
        if (!existing.getName().equals(name) && roleRepository.existsByName(name)) {
            throw new IllegalArgumentException("角色名称已存在: " + name);
        }
        existing.setName(name);
        existing.setDescription(description != null ? description : "");
        return roleRepository.save(existing);
    }

    public Optional<SecurityRole> findById(Long id) {
        return roleRepository.findById(id);
    }

    public IPage<SecurityRole> findPage(long page, long size, String keyword) {
        return roleRepository.findPage(page, size, keyword);
    }

    @Transactional
    public void deleteRole(Long id) {
        rolePermissionRepository.deleteByRoleId(id);
        roleRepository.deleteById(id);
    }

    @Transactional
    public void assignPermission(Long roleId, Long permissionId) {
        List<RolePermission> existing = rolePermissionRepository.findByRoleId(roleId);
        boolean alreadyAssigned = existing.stream()
                .anyMatch(rp -> rp.getPermissionId().equals(permissionId));
        if (alreadyAssigned) {
            return;
        }
        RolePermission rp = RolePermission.builder()
                .roleId(roleId)
                .permissionId(permissionId)
                .build();
        rolePermissionRepository.save(rp);
    }

    @Transactional
    public void removePermission(Long roleId, Long permissionId) {
        List<RolePermission> existing = rolePermissionRepository.findByRoleId(roleId);
        for (RolePermission rp : existing) {
            if (rp.getPermissionId().equals(permissionId)) {
                rolePermissionRepository.deleteByRoleId(roleId);
                // Re-save remaining
                for (RolePermission keep : existing) {
                    if (!keep.getPermissionId().equals(permissionId)) {
                        rolePermissionRepository.save(RolePermission.builder()
                                .roleId(roleId)
                                .permissionId(keep.getPermissionId())
                                .build());
                    }
                }
                return;
            }
        }
    }

    public List<RolePermission> getRolePermissions(Long roleId) {
        return rolePermissionRepository.findByRoleId(roleId);
    }
}