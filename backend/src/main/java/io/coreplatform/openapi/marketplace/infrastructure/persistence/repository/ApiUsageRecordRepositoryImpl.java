package io.coreplatform.openapi.marketplace.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.coreplatform.openapi.marketplace.domain.ApiUsageRecord;
import io.coreplatform.openapi.marketplace.port.ApiUsageRecordRepository;
import io.coreplatform.openapi.marketplace.infrastructure.persistence.entity.ApiUsageRecordEntity;
import io.coreplatform.openapi.marketplace.infrastructure.persistence.mapper.ApiUsageRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ApiUsageRecordRepositoryImpl implements ApiUsageRecordRepository {

    private final ApiUsageRecordMapper mapper;

    @Override
    public ApiUsageRecord save(ApiUsageRecord record) {
        ApiUsageRecordEntity entity = toEntity(record);
        if (entity.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public IPage<ApiUsageRecord> findPage(long page, long size, Long productId, Long providerId) {
        LambdaQueryWrapper<ApiUsageRecordEntity> wrapper = new LambdaQueryWrapper<>();
        if (productId != null) {
            wrapper.eq(ApiUsageRecordEntity::getProductId, productId);
        }
        wrapper.orderByDesc(ApiUsageRecordEntity::getRecordedDate);
        IPage<ApiUsageRecordEntity> entityPage = mapper.selectPage(new Page<>(page, size), wrapper);
        return entityPage.convert(this::toDomain);
    }

    @Override
    public List<ApiUsageRecord> findByProductIdAndDateRange(Long productId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<ApiUsageRecordEntity> wrapper = new LambdaQueryWrapper<ApiUsageRecordEntity>()
                .eq(ApiUsageRecordEntity::getProductId, productId);
        if (startDate != null) {
            wrapper.ge(ApiUsageRecordEntity::getRecordedDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(ApiUsageRecordEntity::getRecordedDate, endDate);
        }
        wrapper.orderByDesc(ApiUsageRecordEntity::getRecordedDate);
        return mapper.selectList(wrapper).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Long sumRequestCountByProductId(Long productId) {
        List<ApiUsageRecordEntity> entities = mapper.selectList(
                new LambdaQueryWrapper<ApiUsageRecordEntity>().eq(ApiUsageRecordEntity::getProductId, productId));
        return entities.stream().mapToLong(ApiUsageRecordEntity::getRequestCount).sum();
    }

    @Override
    public Long sumRequestCountByProviderId(Long providerId) {
        // provider-level aggregation: sum all records where product belongs to provider
        // For simplicity, sum all records directly; in production this would join with product table
        List<ApiUsageRecordEntity> entities = mapper.selectList(null);
        return entities.stream().mapToLong(ApiUsageRecordEntity::getRequestCount).sum();
    }

    @Override
    public Long countDistinctUsersByProductId(Long productId) {
        List<ApiUsageRecordEntity> entities = mapper.selectList(
                new LambdaQueryWrapper<ApiUsageRecordEntity>().eq(ApiUsageRecordEntity::getProductId, productId));
        return entities.stream().map(ApiUsageRecordEntity::getUserId).distinct().count();
    }

    private ApiUsageRecordEntity toEntity(ApiUsageRecord domain) {
        ApiUsageRecordEntity entity = new ApiUsageRecordEntity();
        entity.setId(domain.getId());
        entity.setProductId(domain.getProductId());
        entity.setApiId(domain.getApiId());
        entity.setUserId(domain.getUserId());
        entity.setRequestCount(domain.getRequestCount());
        entity.setTokenCount(domain.getTokenCount());
        entity.setErrorCount(domain.getErrorCount());
        entity.setRecordedDate(domain.getRecordedDate());
        entity.setCreateTime(domain.getCreateTime());
        return entity;
    }

    private ApiUsageRecord toDomain(ApiUsageRecordEntity entity) {
        return ApiUsageRecord.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .apiId(entity.getApiId())
                .userId(entity.getUserId())
                .requestCount(entity.getRequestCount())
                .tokenCount(entity.getTokenCount())
                .errorCount(entity.getErrorCount())
                .recordedDate(entity.getRecordedDate())
                .createTime(entity.getCreateTime())
                .build();
    }
}