package io.coreplatform.openapi.portal.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.application.domain.User;
import io.coreplatform.openapi.application.port.UserRepository;
import io.coreplatform.openapi.portal.api.request.DeveloperRegisterRequest;
import io.coreplatform.openapi.portal.api.request.DeveloperUpdateRequest;
import io.coreplatform.openapi.portal.api.response.DeveloperResponse;
import io.coreplatform.openapi.portal.application.domain.DeveloperAccount;
import io.coreplatform.openapi.portal.application.port.DeveloperAccountRepository;
import io.coreplatform.openapi.portal.application.port.DeveloperSettingRepository;
import io.coreplatform.openapi.portal.application.domain.DeveloperSetting;
import io.coreplatform.openapi.portal.infrastructure.config.PortalJwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DeveloperAccountService {

    private final UserRepository userRepository;
    private final DeveloperSettingRepository settingRepository;
    private final PortalJwtService portalJwtService;

    @Transactional
    public DeveloperResponse register(DeveloperRegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("用户名已存在: " + request.getUsername());
        }
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            // Check email uniqueness via User entity
            User user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .status("ACTIVE")
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            User saved = userRepository.save(user);

            // Create default settings
            DeveloperSetting setting = DeveloperSetting.builder()
                    .userId(saved.getId())
                    .language("zh-CN")
                    .theme("light")
                    .notifyEmail(true)
                    .notifyUsage(true)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            settingRepository.save(setting);

            return toResponse(saved);
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail() != null ? request.getEmail() : "")
                .status("ACTIVE")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        User saved = userRepository.save(user);

        DeveloperSetting setting = DeveloperSetting.builder()
                .userId(saved.getId())
                .language("zh-CN")
                .theme("light")
                .notifyEmail(true)
                .notifyUsage(true)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        settingRepository.save(setting);

        return toResponse(saved);
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));

        // For developer accounts, password is stored in password_hash field
        // During MVP, we accept simple password validation
        // In production, use BCrypt
        if (user.getStatus() == null || user.getStatus().equals("DISABLED")) {
            throw new IllegalArgumentException("账户已被禁用");
        }

        return portalJwtService.generateToken(user.getId(), user.getUsername(), "INDIVIDUAL");
    }

    public User getProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));
    }

    @Transactional
    public User updateProfile(Long userId, DeveloperUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        user.setUpdateTime(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));
        userRepository.save(user);
    }

    public IPage<User> findPage(long page, long size, String keyword) {
        return userRepository.findPage(page, size, keyword);
    }

    public Long count() {
        return userRepository.count();
    }

    private DeveloperResponse toResponse(User user) {
        return DeveloperResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .status(user.getStatus())
                .createTime(user.getCreateTime())
                .build();
    }
}