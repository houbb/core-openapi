package io.coreplatform.openapi.portal.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.application.domain.User;
import io.coreplatform.openapi.portal.api.request.*;
import io.coreplatform.openapi.portal.api.response.DeveloperLoginResponse;
import io.coreplatform.openapi.portal.api.response.DeveloperResponse;
import io.coreplatform.openapi.portal.application.service.DeveloperAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/portal/account")
@RequiredArgsConstructor
@Tag(name = "Developer Account", description = "开发者账号管理")
public class DeveloperAccountController {

    private final DeveloperAccountService accountService;

    @PostMapping("/register")
    @Operation(summary = "注册开发者账号")
    public DeveloperResponse register(@Valid @RequestBody DeveloperRegisterRequest request) {
        return accountService.register(request);
    }

    @PostMapping("/login")
    @Operation(summary = "开发者登录")
    public DeveloperLoginResponse login(@Valid @RequestBody DeveloperLoginRequest request) {
        String token = accountService.login(request.getUsername(), request.getPassword());
        User user = accountService.getProfile(
                accountService.findPage(1, 1, request.getUsername())
                        .getRecords().stream()
                        .filter(u -> u.getUsername().equals(request.getUsername()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("用户不存在"))
                        .getId()
        );

        DeveloperResponse profile = DeveloperResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .status(user.getStatus())
                .createTime(user.getCreateTime())
                .updateTime(user.getUpdateTime())
                .build();

        return DeveloperLoginResponse.builder()
                .token(token)
                .profile(profile)
                .build();
    }

    @GetMapping("/profile")
    @Operation(summary = "获取开发者信息")
    public DeveloperResponse profile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("portalUserId");
        User user = accountService.getProfile(userId);
        return DeveloperResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .status(user.getStatus())
                .createTime(user.getCreateTime())
                .updateTime(user.getUpdateTime())
                .build();
    }

    @PutMapping("/profile")
    @Operation(summary = "更新开发者信息")
    public DeveloperResponse updateProfile(HttpServletRequest request,
                                            @Valid @RequestBody DeveloperUpdateRequest req) {
        Long userId = (Long) request.getAttribute("portalUserId");
        User user = accountService.updateProfile(userId, req);
        return DeveloperResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .status(user.getStatus())
                .createTime(user.getCreateTime())
                .updateTime(user.getUpdateTime())
                .build();
    }

    @PostMapping("/password")
    @Operation(summary = "修改密码")
    public void changePassword(HttpServletRequest request,
                               @Valid @RequestBody ChangePasswordRequest req) {
        Long userId = (Long) request.getAttribute("portalUserId");
        accountService.changePassword(userId, req.getOldPassword(), req.getNewPassword());
    }
}