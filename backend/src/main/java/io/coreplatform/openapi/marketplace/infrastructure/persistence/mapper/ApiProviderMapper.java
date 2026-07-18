package io.coreplatform.openapi.marketplace.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.coreplatform.openapi.marketplace.infrastructure.persistence.entity.ApiProviderEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApiProviderMapper extends BaseMapper<ApiProviderEntity> {
}
