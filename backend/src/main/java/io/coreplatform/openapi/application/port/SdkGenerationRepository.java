package io.coreplatform.openapi.application.port;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.sdk.domain.SdkGeneration;

import java.util.Optional;

public interface SdkGenerationRepository {
    SdkGeneration save(SdkGeneration generation);
    Optional<SdkGeneration> findById(Long id);
    IPage<SdkGeneration> findByProjectId(Long projectId, long page, long size);
    Optional<SdkGeneration> findLatestByProjectId(Long projectId);
}