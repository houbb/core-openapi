package io.coreplatform.openapi.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.api.exception.ResourceNotFoundException;
import io.coreplatform.openapi.api.request.UserRequest;
import io.coreplatform.openapi.api.response.UserResponse;
import io.coreplatform.openapi.application.command.CreateUserCommand;
import io.coreplatform.openapi.application.domain.User;
import io.coreplatform.openapi.application.service.UserApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/openapi/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "用户管理")
public class UserController {

    private final UserApplicationService userApplicationService;

    @GetMapping
    @Operation(summary = "获取用户列表")
    public PageResult<UserResponse> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size,
            @RequestParam(required = false) String keyword) {
        IPage<User> result = userApplicationService.findPage(page, size, keyword);
        return PageResult.of(
                result.getRecords().stream().map(this::toResponse).toList(),
                page, size, result.getTotal()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情")
    public UserResponse get(@PathVariable Long id) {
        User user = userApplicationService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return toResponse(user);
    }

    @PostMapping
    @Operation(summary = "创建用户")
    public UserResponse create(@Valid @RequestBody UserRequest request) {
        CreateUserCommand command = new CreateUserCommand();
        command.setUsername(request.getUsername());
        command.setEmail(request.getEmail());
        command.setStatus(request.getStatus());

        User user = userApplicationService.createUser(command);
        return toResponse(user);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户")
    public UserResponse update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        CreateUserCommand command = new CreateUserCommand();
        command.setUsername(request.getUsername());
        command.setEmail(request.getEmail());
        command.setStatus(request.getStatus());

        User user = userApplicationService.updateUser(id, command);
        return toResponse(user);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public void delete(@PathVariable Long id) {
        userApplicationService.deleteUser(id);
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .status(user.getStatus())
                .createTime(user.getCreateTime())
                .updateTime(user.getUpdateTime())
                .build();
    }
}
