package io.coreplatform.openapi.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("security_role")
public class SecurityRoleEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String tenantId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}