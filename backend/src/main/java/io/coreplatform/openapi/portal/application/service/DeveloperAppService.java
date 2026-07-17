package io.coreplatform.openapi.portal.application.service;

import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.application.command.CreateApplicationCommand;
import io.coreplatform.openapi.application.domain.Application;
import io.coreplatform.openapi.application.port.ApplicationRepository;
import io.coreplatform.openapi.portal.api.request.CreateAppRequest;
import io.coreplatform.openapi.portal.api.response.DeveloperAppResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeveloperAppService {

    private final ApplicationRepository applicationRepository;

    public PageResult<DeveloperAppResponse> listMyApps(Long userId, long page, long size) {
        var appPage = applicationRepository.findPage(page, size, null);
        List<DeveloperAppResponse> items = appPage.getRecords().stream()
                .filter(a -> a.getOwnerId() != null && a.getOwnerId().equals(userId))
                .map(this::toResponse)
                .collect(Collectors.toList());
        return PageResult.of(items, page, size, items.size());
    }

    public DeveloperAppResponse getAppDetail(Long userId, Long appId) {
        Application app = applicationRepository.findById(appId)
                .orElseThrow(() -> new IllegalArgumentException("应用不存在: " + appId));
        if (!userId.equals(app.getOwnerId())) {
            throw new IllegalArgumentException("无权访问此应用");
        }
        return toResponse(app);
    }

    @Transactional
    public DeveloperAppResponse createApp(Long userId, CreateAppRequest request) {
        CreateApplicationCommand command = new CreateApplicationCommand();
        command.setAppName(request.getAppName());
        command.setAppCode("APP_" + System.currentTimeMillis());
        command.setOwnerId(userId);
        command.setDescription(request.getDescription());

        Application app = Application.builder()
                .appName(command.getAppName())
                .appCode(command.getAppCode())
                .ownerId(command.getOwnerId())
                .description(command.getDescription())
                .status("ACTIVE")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        Application saved = applicationRepository.save(app);
        return toResponse(saved);
    }

    @Transactional
    public DeveloperAppResponse updateApp(Long userId, Long appId, CreateAppRequest request) {
        Application app = applicationRepository.findById(appId)
                .orElseThrow(() -> new IllegalArgumentException("应用不存在: " + appId));
        if (!userId.equals(app.getOwnerId())) {
            throw new IllegalArgumentException("无权修改此应用");
        }
        if (request.getAppName() != null) {
            app.setAppName(request.getAppName());
        }
        if (request.getDescription() != null) {
            app.setDescription(request.getDescription());
        }
        app.setUpdateTime(LocalDateTime.now());
        Application saved = applicationRepository.save(app);
        return toResponse(saved);
    }

    @Transactional
    public void deleteApp(Long userId, Long appId) {
        Application app = applicationRepository.findById(appId)
                .orElseThrow(() -> new IllegalArgumentException("应用不存在: " + appId));
        if (!userId.equals(app.getOwnerId())) {
            throw new IllegalArgumentException("无权删除此应用");
        }
        applicationRepository.deleteById(appId);
    }

    private DeveloperAppResponse toResponse(Application app) {
        return DeveloperAppResponse.builder()
                .id(app.getId())
                .appName(app.getAppName())
                .appCode(app.getAppCode())
                .description(app.getDescription())
                .status(app.getStatus())
                .keyCount(0)
                .subscriptionCount(0)
                .createTime(app.getCreateTime())
                .build();
    }
}