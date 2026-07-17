package io.coreplatform.openapi.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.application.command.CreateUserCommand;
import io.coreplatform.openapi.application.domain.User;
import io.coreplatform.openapi.application.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserRepository userRepository;

    @Transactional
    public User createUser(CreateUserCommand command) {
        if (userRepository.existsByUsername(command.getUsername())) {
            throw new IllegalArgumentException("用户名已存在: " + command.getUsername());
        }

        User user = User.builder()
                .username(command.getUsername())
                .email(command.getEmail() != null ? command.getEmail() : "")
                .status(command.getStatus() != null ? command.getStatus() : "ACTIVE")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, CreateUserCommand command) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + id));

        if (!existing.getUsername().equals(command.getUsername())
                && userRepository.existsByUsername(command.getUsername())) {
            throw new IllegalArgumentException("用户名已存在: " + command.getUsername());
        }

        existing.setUsername(command.getUsername());
        existing.setEmail(command.getEmail() != null ? command.getEmail() : "");
        if (command.getStatus() != null) {
            existing.setStatus(command.getStatus());
        }
        existing.setUpdateTime(LocalDateTime.now());
        return userRepository.save(existing);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public IPage<User> findPage(long page, long size, String keyword) {
        return userRepository.findPage(page, size, keyword);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Long count() {
        return userRepository.count();
    }
}