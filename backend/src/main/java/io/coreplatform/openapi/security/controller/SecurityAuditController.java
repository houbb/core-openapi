package io.coreplatform.openapi.security.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.api.response.RiskEventResponse;
import io.coreplatform.openapi.api.response.SecurityAuditLogResponse;
import io.coreplatform.openapi.security.domain.RiskEvent;
import io.coreplatform.openapi.security.domain.SecurityAuditLog;
import io.coreplatform.openapi.security.port.RiskEventRepository;
import io.coreplatform.openapi.security.port.SecurityAuditLogRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/openapi/security/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Security Audit Management", description = "安全审计管理")
public class SecurityAuditController {

    private final SecurityAuditLogRepository auditLogRepository;
    private final RiskEventRepository riskEventRepository;

    @GetMapping
    @Operation(summary = "获取审计日志列表")
    public PageResult<SecurityAuditLogResponse> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "50") long size,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String identityId) {
        IPage<SecurityAuditLog> result = auditLogRepository.findPage(
                page, size, eventType, identityId, null, null);
        return PageResult.of(
                result.getRecords().stream().map(this::toResponse).toList(),
                page, size, result.getTotal()
        );
    }

    @GetMapping("/stats")
    @Operation(summary = "获取审计统计")
    public AuditStats getStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        return new AuditStats(
                auditLogRepository.countByEventType("AUTH_FAILURE", todayStart, now),
                auditLogRepository.countByEventType("PERMISSION_DENIED", todayStart, now),
                auditLogRepository.countByEventType("RISK_ALERT", todayStart, now)
        );
    }

    @GetMapping("/risk-events")
    @Operation(summary = "获取风控事件列表")
    public PageResult<RiskEventResponse> listRiskEvents(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "50") long size,
            @RequestParam(required = false) String riskType,
            @RequestParam(required = false) String identityId) {
        IPage<RiskEvent> result = riskEventRepository.findPage(page, size, riskType, identityId);
        return PageResult.of(
                result.getRecords().stream().map(this::toRiskEventResponse).toList(),
                page, size, result.getTotal()
        );
    }

    private SecurityAuditLogResponse toResponse(SecurityAuditLog log) {
        return SecurityAuditLogResponse.builder()
                .id(log.getId())
                .eventType(log.getEventType())
                .identityType(log.getIdentityType())
                .identityId(log.getIdentityId())
                .resourceType(log.getResourceType())
                .resourceId(log.getResourceId())
                .result(log.getResult())
                .detail(log.getDetail())
                .ip(log.getIp())
                .requestId(log.getRequestId())
                .tenantId(log.getTenantId())
                .createdTime(log.getCreatedTime())
                .build();
    }

    private RiskEventResponse toRiskEventResponse(RiskEvent event) {
        return RiskEventResponse.builder()
                .id(event.getId())
                .identityId(event.getIdentityId())
                .riskType(event.getRiskType())
                .severity(event.getSeverity())
                .detail(event.getDetail())
                .requestCount(event.getRequestCount())
                .windowSeconds(event.getWindowSeconds())
                .thresholdCount(event.getThresholdCount())
                .tenantId(event.getTenantId())
                .createdTime(event.getCreatedTime())
                .build();
    }

    public record AuditStats(long authFailures, long permissionDenied, long riskAlerts) {
    }
}