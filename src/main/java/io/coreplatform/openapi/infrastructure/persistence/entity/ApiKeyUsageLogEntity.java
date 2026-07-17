package io.coreplatform.openapi.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("api_key_usage_log")
public class ApiKeyUsageLogEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long apiKeyId;
    private Long apiId;
    private String requestId;
    private String ip;
    private LocalDateTime timestamp;
    private String status;
}
