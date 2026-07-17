package io.coreplatform.openapi.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.coreplatform.openapi.infrastructure.persistence.entity.DefinitionEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DefinitionMapper extends BaseMapper<DefinitionEntity> {
}
