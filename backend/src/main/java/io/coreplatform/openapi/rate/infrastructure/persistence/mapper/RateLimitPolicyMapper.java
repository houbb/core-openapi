package io.coreplatform.openapi.rate.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.coreplatform.openapi.rate.infrastructure.persistence.entity.RateLimitPolicyEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RateLimitPolicyMapper extends BaseMapper<RateLimitPolicyEntity> {
}