package io.coreplatform.openapi.enterprise.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.coreplatform.openapi.enterprise.infrastructure.persistence.entity.CompliancePolicyEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CompliancePolicyMapper extends BaseMapper<CompliancePolicyEntity> {
}