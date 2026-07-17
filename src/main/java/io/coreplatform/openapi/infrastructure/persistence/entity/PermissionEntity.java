package io.coreplatform.openapi.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("api_permission")
public class PermissionEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
}
