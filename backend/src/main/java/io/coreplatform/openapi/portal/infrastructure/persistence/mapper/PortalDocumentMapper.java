package io.coreplatform.openapi.portal.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.coreplatform.openapi.portal.infrastructure.persistence.entity.PortalDocumentEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PortalDocumentMapper extends BaseMapper<PortalDocumentEntity> {
}
