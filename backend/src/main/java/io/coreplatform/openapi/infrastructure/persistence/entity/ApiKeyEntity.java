package io.coreplatform.openapi.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("openapi_api_key")
public class ApiKeyEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long applicationId;
    private String keyPrefix;
    private String keyHash;
    private String name;
    private String environment;
    private String status;
    private LocalDateTime expireTime;
    private LocalDateTime lastUsedTime;
    private LocalDateTime createdTime;
    private String tenantId;
}