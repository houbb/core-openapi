package io.coreplatform.openapi.rate.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.coreplatform.openapi.rate.infrastructure.persistence.entity.RateLimitUsageEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RateLimitUsageMapper extends BaseMapper<RateLimitUsageEntity> {
}