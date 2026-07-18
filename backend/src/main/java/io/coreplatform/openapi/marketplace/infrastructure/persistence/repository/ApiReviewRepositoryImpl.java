package io.coreplatform.openapi.marketplace.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.coreplatform.openapi.marketplace.domain.ApiReview;
import io.coreplatform.openapi.marketplace.port.ApiReviewRepository;
import io.coreplatform.openapi.marketplace.infrastructure.persistence.entity.ApiReviewEntity;
import io.coreplatform.openapi.marketplace.infrastructure.persistence.mapper.ApiReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ApiReviewRepositoryImpl implements ApiReviewRepository {

    private final ApiReviewMapper mapper;

    @Override
    public ApiReview save(ApiReview review) {
        ApiReviewEntity entity = toEntity(review);
        if (entity.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<ApiReview> findById(Long id) {
        ApiReviewEntity entity = mapper.selectById(id);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public IPage<ApiReview> findPage(long page, long size, Long productId) {
        LambdaQueryWrapper<ApiReviewEntity> wrapper = new LambdaQueryWrapper<>();
        if (productId != null) {
            wrapper.eq(ApiReviewEntity::getProductId, productId);
        }
        wrapper.orderByDesc(ApiReviewEntity::getCreateTime);
        IPage<ApiReviewEntity> entityPage = mapper.selectPage(new Page<>(page, size), wrapper);
        return entityPage.convert(this::toDomain);
    }

    @Override
    public List<ApiReview> findByProductId(Long productId) {
        List<ApiReviewEntity> entities = mapper.selectList(
                new LambdaQueryWrapper<ApiReviewEntity>()
                        .eq(ApiReviewEntity::getProductId, productId)
                        .orderByDesc(ApiReviewEntity::getCreateTime));
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        mapper.deleteById(id);
    }

    @Override
    public Double avgRatingByProductId(Long productId) {
        List<ApiReviewEntity> entities = mapper.selectList(
                new LambdaQueryWrapper<ApiReviewEntity>().eq(ApiReviewEntity::getProductId, productId));
        if (entities.isEmpty()) {
            return 0.0;
        }
        return entities.stream().mapToInt(ApiReviewEntity::getRating).average().orElse(0.0);
    }

    @Override
    public Long countByProductId(Long productId) {
        return mapper.selectCount(
                new LambdaQueryWrapper<ApiReviewEntity>().eq(ApiReviewEntity::getProductId, productId));
    }

    private ApiReviewEntity toEntity(ApiReview domain) {
        ApiReviewEntity entity = new ApiReviewEntity();
        entity.setId(domain.getId());
        entity.setProductId(domain.getProductId());
        entity.setUserId(domain.getUserId());
        entity.setRating(domain.getRating());
        entity.setComment(domain.getComment());
        entity.setCreateTime(domain.getCreateTime());
        return entity;
    }

    private ApiReview toDomain(ApiReviewEntity entity) {
        return ApiReview.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .userId(entity.getUserId())
                .rating(entity.getRating())
                .comment(entity.getComment())
                .createTime(entity.getCreateTime())
                .build();
    }
}