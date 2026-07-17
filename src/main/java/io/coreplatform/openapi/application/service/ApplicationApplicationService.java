package io.coreplatform.openapi.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.application.command.CreateApplicationCommand;
import io.coreplatform.openapi.application.domain.Application;
import io.coreplatform.openapi.application.port.ApplicationPermissionRepository;
import io.coreplatform.openapi.application.port.ApplicationRepository;
import io.coreplatform.openapi.application.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final ApplicationPermissionRepository applicationPermissionRepository;

    @Transactional
    public Application createApplication(CreateApplicationCommand command) {
        if (applicationRepository.existsByAppCode(command.getAppCode())) {
            throw new IllegalArgumentException("应用编码已存在: " + command.getAppCode());
        }

        if (command.getOwnerId() != null) {
            userRepository.findById(command.getOwnerId())
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + command.getOwnerId()));
        }

        Application application = Application.builder()
                .appName(command.getAppName())
                .appCode(command.getAppCode())
                .ownerId(command.getOwnerId())
                .description(command.getDescription() != null ? command.getDescription() : "")
                .status(command.getStatus() != null ? command.getStatus() : "ACTIVE")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        return applicationRepository.save(application);
    }

    @Transactional
    public Application updateApplication(Long id, CreateApplicationCommand command) {
        Application existing = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("应用不存在: " + id));

        if (!existing.getAppCode().equals(command.getAppCode())
                && applicationRepository.existsByAppCode(command.getAppCode())) {
            throw new IllegalArgumentException("应用编码已存在: " + command.getAppCode());
        }

        if (command.getOwnerId() != null) {
            userRepository.findById(command.getOwnerId())
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + command.getOwnerId()));
        }

        existing.setAppName(command.getAppName());
        existing.setAppCode(command.getAppCode());
        existing.setOwnerId(command.getOwnerId());
        existing.setDescription(command.getDescription() != null ? command.getDescription() : "");
        if (command.getStatus() != null) {
            existing.setStatus(command.getStatus());
        }
        existing.setUpdateTime(LocalDateTime.now());
        return applicationRepository.save(existing);
    }

    public Optional<Application> findById(Long id) {
        return applicationRepository.findById(id);
    }

    public IPage<Application> findPage(long page, long size, String keyword) {
        return applicationRepository.findPage(page, size, keyword);
    }

    @Transactional
    public void deleteApplication(Long id) {
        // Clean up associated permissions
        applicationPermissionRepository.deleteByApplicationId(id);
        applicationRepository.deleteById(id);
    }

    public Long count() {
        return applicationRepository.count();
    }
}