package io.coreplatform.openapi.marketplace.port;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.marketplace.domain.ApiUsageRecord;

import java.time.LocalDate;
import java.util.List;

public interface ApiUsageRecordRepository {
    ApiUsageRecord save(ApiUsageRecord record);
    IPage<ApiUsageRecord> findPage(long page, long size, Long productId, Long providerId);
    List<ApiUsageRecord> findByProductIdAndDateRange(Long productId, LocalDate startDate, LocalDate endDate);
    Long sumRequestCountByProductId(Long productId);
    Long sumRequestCountByProviderId(Long providerId);
    Long countDistinctUsersByProductId(Long productId);
}
