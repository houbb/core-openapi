package io.coreplatform.openapi.application.port;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.sdk.domain.SdkProject;

import java.util.Optional;

public interface SdkProjectRepository {
    SdkProject save(SdkProject project);
    Optional<SdkProject> findById(Long id);
    IPage<SdkProject> findPage(long page, long size);
    void deleteById(Long id);
}