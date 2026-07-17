package io.coreplatform.openapi.application.port;

import io.coreplatform.openapi.application.domain.ApplicationPermission;

import java.util.List;

public interface ApplicationPermissionRepository {

    ApplicationPermission save(ApplicationPermission applicationPermission);

    List<ApplicationPermission> findByApplicationId(Long applicationId);

    List<ApplicationPermission> findByPermissionId(Long permissionId);

    void delete(Long applicationId, Long permissionId);

    boolean exists(Long applicationId, Long permissionId);

    void deleteByApplicationId(Long applicationId);
}