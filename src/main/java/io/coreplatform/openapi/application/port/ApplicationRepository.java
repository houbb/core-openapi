package io.coreplatform.openapi.application.port;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.application.domain.Application;

import java.util.Optional;

public interface ApplicationRepository {

    Application save(Application application);

    Optional<Application> findById(Long id);

    Optional<Application> findByAppCode(String appCode);

    IPage<Application> findPage(long page, long size, String keyword);

    void deleteById(Long id);

    boolean existsByAppCode(String appCode);

    Long count();
}
