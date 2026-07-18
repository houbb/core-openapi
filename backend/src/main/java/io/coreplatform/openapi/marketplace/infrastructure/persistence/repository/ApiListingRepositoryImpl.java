package io.coreplatform.openapi.marketplace.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.coreplatform.openapi.marketplace.domain.ApiListing;
import io.coreplatform.openapi.marketplace.port.ApiListingRepository;
import io.coreplatform.openapi.marketplace.infrastructure.persistence.entity.ApiListingEntity;
import io.coreplatform.openapi.marketplace.infrastructure.persistence.mapper.ApiListingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ApiListingRepositoryImpl implements ApiListingRepository {

    private final ApiListingMapper mapper;

    @Override
    public ApiListing save(ApiListing listing) {
        ApiListingEntity entity = toEntity(listing);
        if (entity.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<ApiListing> findById(Long id) {
        ApiListingEntity entity = mapper.selectById(id);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public Optional<ApiListing> findByProductId(Long productId) {
        ApiListingEntity entity = mapper.selectOne(
                new LambdaQueryWrapper<ApiListingEntity>().eq(ApiListingEntity::getProductId, productId));
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public List<ApiListing> findFeatured(int limit) {
        LambdaQueryWrapper<ApiListingEntity> wrapper = new LambdaQueryWrapper<ApiListingEntity>()
                .eq(ApiListingEntity::getFeatured, 1)
                .orderByDesc(ApiListingEntity::getSortOrder);
        if (limit > 0) {
            wrapper.last("LIMIT " + limit);
        }
        return mapper.selectList(wrapper).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        mapper.deleteById(id);
    }

    @Override
    public void deleteByProductId(Long productId) {
        mapper.delete(new LambdaQueryWrapper<ApiListingEntity>().eq(ApiListingEntity::getProductId, productId));
    }

    private ApiListingEntity toEntity(ApiListing domain) {
        ApiListingEntity entity = new ApiListingEntity();
        entity.setId(domain.getId());
        entity.setProductId(domain.getProductId());
        entity.setFeatured(domain.getFeatured());
        entity.setSortOrder(domain.getSortOrder());
        entity.setTags(domain.getTags());
        entity.setHighlightText(domain.getHighlightText());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        return entity;
    }

    private ApiListing toDomain(ApiListingEntity entity) {
        return ApiListing.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .featured(entity.getFeatured())
                .sortOrder(entity.getSortOrder())
                .tags(entity.getTags())
                .highlightText(entity.getHighlightText())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}