package io.coreplatform.openapi.enterprise.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.coreplatform.openapi.enterprise.infrastructure.persistence.entity.SlaPolicyEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SlaPolicyMapper extends BaseMapper<SlaPolicyEntity> {
}