package io.coreplatform.openapi.application.port;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.application.domain.User;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    IPage<User> findPage(long page, long size, String keyword);

    void deleteById(Long id);

    boolean existsByUsername(String username);

    Long count();
}
