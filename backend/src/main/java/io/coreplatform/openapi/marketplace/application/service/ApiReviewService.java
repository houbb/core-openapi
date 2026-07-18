package io.coreplatform.openapi.marketplace.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.marketplace.domain.ApiReview;
import io.coreplatform.openapi.marketplace.port.ApiReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiReviewService {

    private final ApiReviewRepository reviewRepository;

    public IPage<ApiReview> findPage(long page, long size, Long productId) {
        return reviewRepository.findPage(page, size, productId);
    }

    public List<ApiReview> findByProductId(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    @Transactional
    public ApiReview createReview(Long productId, Long userId, Integer rating, String comment) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("评分必须在 1-5 之间");
        }
        ApiReview review = ApiReview.builder()
                .productId(productId)
                .userId(userId)
                .rating(rating)
                .comment(comment != null ? comment : "")
                .createTime(LocalDateTime.now())
                .build();
        return reviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    public Double avgRatingByProductId(Long productId) {
        return reviewRepository.avgRatingByProductId(productId);
    }

    public Long countByProductId(Long productId) {
        return reviewRepository.countByProductId(productId);
    }
}