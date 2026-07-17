package io.coreplatform.openapi.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.coreplatform.openapi.application.domain.User;
import io.coreplatform.openapi.application.port.UserRepository;
import io.coreplatform.openapi.infrastructure.persistence.entity.UserEntity;
import io.coreplatform.openapi.infrastructure.persistence.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        if (entity.getId() == null) {
            userMapper.insert(entity);
        } else {
            userMapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<User> findById(Long id) {
        UserEntity entity = userMapper.selectById(id);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        UserEntity entity = userMapper.selectOne(
                new LambdaQueryWrapper<UserEntity>()
                        .eq(UserEntity::getUsername, username)
        );
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public IPage<User> findPage(long page, long size, String keyword) {
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(UserEntity::getUsername, keyword)
                    .or()
                    .like(UserEntity::getEmail, keyword);
        }
        wrapper.orderByDesc(UserEntity::getUpdateTime);

        IPage<UserEntity> entityPage = userMapper.selectPage(new Page<>(page, size), wrapper);
        return entityPage.convert(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        userMapper.deleteById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<UserEntity>()
                        .eq(UserEntity::getUsername, username)
        );
        return count > 0;
    }

    @Override
    public Long count() {
        return userMapper.selectCount(null);
    }

    // ---- Entity <-> Domain conversion ----

    private UserEntity toEntity(User domain) {
        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setUsername(domain.getUsername());
        entity.setEmail(domain.getEmail());
        entity.setStatus(domain.getStatus());
        entity.setTenantId(domain.getTenantId());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        return entity;
    }

    private User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .status(entity.getStatus())
                .tenantId(entity.getTenantId())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}
