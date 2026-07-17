package io.coreplatform.openapi.security.port;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.security.domain.SecurityRole;

import java.util.List;
import java.util.Optional;

public interface SecurityRoleRepository {

    SecurityRole save(SecurityRole role);

    Optional<SecurityRole> findById(Long id);

    Optional<SecurityRole> findByName(String name);

    List<SecurityRole> findByTenantId(String tenantId);

    IPage<SecurityRole> findPage(long page, long size, String keyword);

    void deleteById(Long id);

    boolean existsByName(String name);
}