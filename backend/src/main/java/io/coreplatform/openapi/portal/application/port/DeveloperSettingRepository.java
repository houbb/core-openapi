package io.coreplatform.openapi.portal.application.port;

import io.coreplatform.openapi.portal.application.domain.DeveloperSetting;

import java.util.Optional;

public interface DeveloperSettingRepository {
    DeveloperSetting save(DeveloperSetting setting);
    Optional<DeveloperSetting> findByUserId(Long userId);
}
