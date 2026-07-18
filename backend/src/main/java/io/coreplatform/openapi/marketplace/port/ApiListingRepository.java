package io.coreplatform.openapi.marketplace.port;

import io.coreplatform.openapi.marketplace.domain.ApiListing;

import java.util.List;
import java.util.Optional;

public interface ApiListingRepository {
    ApiListing save(ApiListing listing);
    Optional<ApiListing> findById(Long id);
    Optional<ApiListing> findByProductId(Long productId);
    List<ApiListing> findFeatured(int limit);
    void deleteById(Long id);
    void deleteByProductId(Long productId);
}
