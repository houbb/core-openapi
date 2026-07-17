package io.coreplatform.openapi.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.application.command.CreatePermissionCommand;
import io.coreplatform.openapi.application.domain.Permission;
import io.coreplatform.openapi.application.port.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PermissionApplicationService {

    private final PermissionRepository permissionRepository;

    @Transactional
    public Permission createPermission(CreatePermissionCommand command) {
        if (permissionRepository.existsByName(command.getName())) {
            throw new IllegalArgumentException("权限名称已存在: " + command.getName());
        }

        Permission permission = Permission.builder()
                .name(command.getName())
                .description(command.getDescription() != null ? command.getDescription() : "")
                .build();
        return permissionRepository.save(permission);
    }

    @Transactional
    public Permission updatePermission(Long id, CreatePermissionCommand command) {
        Permission existing = permissionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("权限不存在: " + id));

        if (!existing.getName().equals(command.getName())
                && permissionRepository.existsByName(command.getName())) {
            throw new IllegalArgumentException("权限名称已存在: " + command.getName());
        }

        existing.setName(command.getName());
        existing.setDescription(command.getDescription() != null ? command.getDescription() : "");
        return permissionRepository.save(existing);
    }

    public Optional<Permission> findById(Long id) {
        return permissionRepository.findById(id);
    }

    public IPage<Permission> findPage(long page, long size, String keyword) {
        return permissionRepository.findPage(page, size, keyword);
    }

    @Transactional
    public void deletePermission(Long id) {
        permissionRepository.deleteById(id);
    }
}
