package io.coreplatform.openapi.security.port;

import io.coreplatform.openapi.security.domain.ApplicationRole;

import java.util.List;

public interface ApplicationRoleRepository {

    ApplicationRole save(ApplicationRole applicationRole);

    List<ApplicationRole> findByApplicationId(Long applicationId);

    List<ApplicationRole> findByRoleId(Long roleId);

    void delete(Long applicationId, Long roleId);

    void deleteByApplicationId(Long applicationId);

    void batchSave(List<ApplicationRole> list);
}