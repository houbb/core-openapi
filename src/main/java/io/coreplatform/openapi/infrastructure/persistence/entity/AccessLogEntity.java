package io.coreplatform.openapi.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("openapi_access_log")
public class AccessLogEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String requestId;
    private Long apiId;
    private String clientId;
    private String requestMethod;
    private String requestPath;
    private String targetUrl;
    private LocalDateTime requestTime;
    private LocalDateTime responseTime;
    private Integer statusCode;
    private Long costTime;
    private String errorMessage;
    private LocalDateTime createTime;
}