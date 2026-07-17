package io.coreplatform.openapi.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("openapi_user")
public class UserEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String email;
    private String status;
    private String tenantId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
