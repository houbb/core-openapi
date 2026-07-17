package io.coreplatform.openapi.security.port;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.security.domain.ApiSecurityPolicy;

import java.util.Optional;

public interface ApiSecurityPolicyRepository {

    ApiSecurityPolicy save(ApiSecurityPolicy policy);

    Optional<ApiSecurityPolicy> findByApiId(Long apiId);

    Optional<ApiSecurityPolicy> findById(Long id);

    IPage<ApiSecurityPolicy> findPage(long page, long size);

    void deleteById(Long id);
}