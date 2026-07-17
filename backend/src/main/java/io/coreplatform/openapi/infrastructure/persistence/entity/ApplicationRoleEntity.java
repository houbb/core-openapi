package io.coreplatform.openapi.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("application_role")
public class ApplicationRoleEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long applicationId;
    private Long roleId;
    private LocalDateTime createTime;
}