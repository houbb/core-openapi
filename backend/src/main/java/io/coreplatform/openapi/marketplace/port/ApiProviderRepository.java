package io.coreplatform.openapi.marketplace.port;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.marketplace.domain.ApiProvider;

import java.util.List;
import java.util.Optional;

public interface ApiProviderRepository {
    ApiProvider save(ApiProvider provider);
    Optional<ApiProvider> findById(Long id);
    IPage<ApiProvider> findPage(long page, long size, String keyword, String type, String status);
    List<ApiProvider> findAll();
    void deleteById(Long id);
    Long countByStatus(String status);
    Long countAll();
}
