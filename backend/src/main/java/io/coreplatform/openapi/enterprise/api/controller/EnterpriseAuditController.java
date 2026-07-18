package io.coreplatform.openapi.enterprise.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.infrastructure.persistence.entity.SecurityAuditLogEntity;
import io.coreplatform.openapi.infrastructure.persistence.mapper.SecurityAuditLogMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/openapi/enterprise/audit")
@RequiredArgsConstructor
@Tag(name = "Enterprise Audit", description = "企业审计日志")
public class EnterpriseAuditController {

    private final SecurityAuditLogMapper auditLogMapper;

    @GetMapping("/logs")
    @Operation(summary = "获取审计日志列表")
    public PageResult<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String result,
            @RequestParam(required = false) String identity,
            @RequestParam(required = false) String tenantId) {

        LambdaQueryWrapper<SecurityAuditLogEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(eventType)) {
            wrapper.eq(SecurityAuditLogEntity::getEventType, eventType);
        }
        if (StringUtils.hasText(result)) {
            wrapper.eq(SecurityAuditLogEntity::getResult, result);
        }
        if (StringUtils.hasText(identity)) {
            wrapper.like(SecurityAuditLogEntity::getIdentityId, identity);
        }
        if (StringUtils.hasText(tenantId)) {
            wrapper.eq(SecurityAuditLogEntity::getTenantId, tenantId);
        }
        wrapper.orderByDesc(SecurityAuditLogEntity::getCreatedTime);

        IPage<SecurityAuditLogEntity> entityPage = auditLogMapper.selectPage(new Page<>(page, size), wrapper);

        List<Map<String, Object>> items = entityPage.getRecords().stream().map(e -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", e.getId());
            map.put("eventType", e.getEventType());
            map.put("identityType", e.getIdentityType());
            map.put("identityId", e.getIdentityId());
            map.put("resourceType", e.getResourceType());
            map.put("resourceId", e.getResourceId());
            map.put("result", e.getResult());
            map.put("detail", e.getDetail());
            map.put("ip", e.getIp());
            map.put("requestId", e.getRequestId());
            map.put("tenantId", e.getTenantId());
            map.put("createdTime", e.getCreatedTime() != null ? e.getCreatedTime().toString() : "");
            return map;
        }).toList();

        return PageResult.of(items, page, size, entityPage.getTotal());
    }
}