package io.coreplatform.openapi.enterprise.port;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.enterprise.domain.Organization;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository {
    Organization save(Organization org);
    Optional<Organization> findById(Long id);
    Optional<Organization> findByCode(String code);
    IPage<Organization> findPage(long page, long size, String keyword, String type, String status);
    List<Organization> findByTenantId(String tenantId);
    void deleteById(Long id);
    Long countByStatus(String status);
    Long countAll();
}
