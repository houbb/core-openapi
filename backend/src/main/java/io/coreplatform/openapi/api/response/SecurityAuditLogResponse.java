package io.coreplatform.openapi.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityAuditLogResponse {
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