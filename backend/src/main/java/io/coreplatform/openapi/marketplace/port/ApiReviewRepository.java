package io.coreplatform.openapi.marketplace.port;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.marketplace.domain.ApiReview;

import java.util.List;
import java.util.Optional;

public interface ApiReviewRepository {
    ApiReview save(ApiReview review);
    Optional<ApiReview> findById(Long id);
    IPage<ApiReview> findPage(long page, long size, Long productId);
    List<ApiReview> findByProductId(Long productId);
    void deleteById(Long id);
    Double avgRatingByProductId(Long productId);
    Long countByProductId(Long productId);
}
