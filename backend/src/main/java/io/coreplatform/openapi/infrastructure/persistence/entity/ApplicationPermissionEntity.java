package io.coreplatform.openapi.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("application_permission")
public class ApplicationPermissionEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long applicationId;
    private Long permissionId;
}
