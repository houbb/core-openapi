package io.coreplatform.openapi.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("openapi_tag_mapping")
public class TagMappingEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tagId;
    private Long apiId;
    private LocalDateTime createTime;
}
