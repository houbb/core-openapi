package io.coreplatform.openapi.portal.application.port;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.portal.application.domain.DeveloperAccount;

import java.util.Optional;

public interface DeveloperAccountRepository {
    DeveloperAccount save(DeveloperAccount account);
    Optional<DeveloperAccount> findById(Long id);
    Optional<DeveloperAccount> findByUsername(String username);
    Optional<DeveloperAccount> findByEmail(String email);
    IPage<DeveloperAccount> findPage(long page, long size, String keyword);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Long count();
}