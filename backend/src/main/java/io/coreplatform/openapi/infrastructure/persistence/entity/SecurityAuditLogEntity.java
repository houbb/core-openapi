package io.coreplatform.openapi.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("security_audit_log")
public class SecurityAuditLogEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String eventType;
    private String identityType;
    private String identityId;
    private String resourceType;
    private String resourceId;
    private String result;
    private String detail;
    private String ip;
    private String requestId;
    private String tenantId;
    private LocalDateTime createdTime;
}