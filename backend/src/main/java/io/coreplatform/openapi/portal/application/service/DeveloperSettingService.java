package io.coreplatform.openapi.portal.application.service;

import io.coreplatform.openapi.portal.api.request.SettingUpdateRequest;
import io.coreplatform.openapi.portal.application.domain.DeveloperSetting;
import io.coreplatform.openapi.portal.application.port.DeveloperSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DeveloperSettingService {

    private final DeveloperSettingRepository settingRepository;

    public DeveloperSetting getSettings(Long userId) {
        return settingRepository.findByUserId(userId)
                .orElseGet(() -> {
                    // Create default settings
                    DeveloperSetting defaults = DeveloperSetting.builder()
                            .userId(userId)
                            .language("zh-CN")
                            .theme("light")
                            .notifyEmail(true)
                            .notifyUsage(true)
                            .createTime(LocalDateTime.now())
                            .updateTime(LocalDateTime.now())
                            .build();
                    return settingRepository.save(defaults);
                });
    }

    @Transactional
    public DeveloperSetting updateSettings(Long userId, SettingUpdateRequest request) {
        DeveloperSetting setting = getSettings(userId);

        if (request.getLanguage() != null) setting.setLanguage(request.getLanguage());
        if (request.getTheme() != null) setting.setTheme(request.getTheme());
        if (request.getNotifyEmail() != null) setting.setNotifyEmail(request.getNotifyEmail());
        if (request.getNotifyUsage() != null) setting.setNotifyUsage(request.getNotifyUsage());
        setting.setUpdateTime(LocalDateTime.now());

        return settingRepository.save(setting);
    }
}