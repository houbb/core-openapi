package io.coreplatform.openapi.marketplace.port;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.marketplace.domain.ApiProduct;

import java.util.List;
import java.util.Optional;

public interface ApiProductRepository {
    ApiProduct save(ApiProduct product);
    Optional<ApiProduct> findById(Long id);
    Optional<ApiProduct> findByApiId(Long apiId);
    IPage<ApiProduct> findPage(long page, long size, String keyword, String category, String status, Long providerId);
    List<ApiProduct> findByProviderId(Long providerId);
    List<ApiProduct> findPublished(String category, int limit);
    void deleteById(Long id);
    Long countByStatus(String status);
    Long countAll();
}
