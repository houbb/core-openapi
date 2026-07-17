package io.coreplatform.openapi.security.service;

import io.coreplatform.openapi.security.domain.SecurityAuditLog;
import io.coreplatform.openapi.security.port.SecurityAuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityAuditService {

    private final SecurityAuditLogRepository auditLogRepository;

    @Async
    public void record(String eventType, String identityType, String identityId,
                       String resourceType, String resourceId, String result,
                       String detail, String ip, String requestId, String tenantId) {
        try {
            SecurityAuditLog log = SecurityAuditLog.builder()
                    .eventType(eventType)
                    .identityType(identityType)
                    .identityId(identityId)
                    .resourceType(resourceType)
                    .resourceId(resourceId)
                    .result(result)
                    .detail(detail)
                    .ip(ip)
                    .requestId(requestId)
                    .tenantId(tenantId != null ? tenantId : "")
                    .createdTime(LocalDateTime.now())
                    .build();
            auditLogRepository.save(log);
        } catch (Exception e) {
            log.error("Failed to write security audit log: eventType={}, identityId={}", eventType, identityId, e);
        }
    }

    @Async
    public void recordAuthSuccess(String identityType, String identityId,
                                  String ip, String requestId, String tenantId) {
        record("AUTH_SUCCESS", identityType, identityId, "API", "", "SUCCESS",
                "Authentication successful", ip, requestId, tenantId);
    }

    @Async
    public void recordAuthFailure(String identityType, String identityId,
                                  String reason, String ip, String requestId, String tenantId) {
        record("AUTH_FAILURE", identityType, identityId, "API", "", "FAILURE",
                reason, ip, requestId, tenantId);
    }

    @Async
    public void recordPermissionDenied(String identityType, String identityId,
                                       String requiredPermission, String ip, String requestId, String tenantId) {
        record("PERMISSION_DENIED", identityType, identityId, "API", "", "DENIED",
                "Required permission: " + requiredPermission, ip, requestId, tenantId);
    }

    @Async
    public void recordPolicyDenied(String identityType, String identityId,
                                   String reason, String ip, String requestId, String tenantId) {
        record("POLICY_DENIED", identityType, identityId, "API", "", "DENIED",
                reason, ip, requestId, tenantId);
    }

    @Async
    public void recordRiskAlert(String identityId, String detail, String ip,
                                String requestId, String tenantId) {
        record("RISK_ALERT", "API_KEY", identityId, "API", "", "DENIED",
                detail, ip, requestId, tenantId);
    }
}