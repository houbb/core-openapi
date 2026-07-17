package io.coreplatform.openapi.application.port;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.application.domain.Permission;

import java.util.Optional;

public interface PermissionRepository {

    Permission save(Permission permission);

    Optional<Permission> findById(Long id);

    Optional<Permission> findByName(String name);

    IPage<Permission> findPage(long page, long size, String keyword);

    void deleteById(Long id);

    boolean existsByName(String name);
}
