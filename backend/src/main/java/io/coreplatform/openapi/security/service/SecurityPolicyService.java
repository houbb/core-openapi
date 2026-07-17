package io.coreplatform.openapi.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.coreplatform.openapi.gateway.GatewayErrorCode;
import io.coreplatform.openapi.gateway.GatewayException;
import io.coreplatform.openapi.gateway.RequestContext;
import io.coreplatform.openapi.gateway.RequestContextHolder;
import io.coreplatform.openapi.security.domain.ApiSecurityPolicy;
import io.coreplatform.openapi.security.domain.SecurityPolicyDecision;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityPolicyService {

    private final ObjectMapper objectMapper;

    /**
     * Evaluate security policy for the current request.
     *
     * @param policy  the API security policy (may be null, meaning default allow)
     * @param request the HTTP servlet request
     * @return SecurityPolicyDecision with allowed flag and reason
     * @throws GatewayException if policy check fails
     */
    public SecurityPolicyDecision evaluate(ApiSecurityPolicy policy, HttpServletRequest request) {
        if (policy == null || !"ACTIVE".equals(policy.getStatus())) {
            return SecurityPolicyDecision.builder()
                    .allowed(true)
                    .reason("No active policy")
                    .build();
        }

        // Check tenant isolation
        if (policy.getTenantCheck() != null && policy.getTenantCheck() == 1) {
            RequestContext ctx = RequestContextHolder.current();
            if (ctx == null || ctx.getTenantId() == null || ctx.getTenantId().isBlank()) {
                throw new GatewayException(GatewayErrorCode.TENANT_REQUIRED);
            }
        }

        // Check IP whitelist
        if (policy.getIpWhiteList() != null && !policy.getIpWhiteList().isBlank()) {
            String clientIp = getClientIp(request);
            if (!isIpAllowed(clientIp, policy.getIpWhiteList())) {
                throw new GatewayException(GatewayErrorCode.IP_NOT_ALLOWED,
                        "IP " + clientIp + " 不在白名单中");
            }
        }

        // Check time limit
        if (policy.getTimeLimitEnabled() != null && policy.getTimeLimitEnabled() == 1) {
            if (!isWithinTimeRange(policy.getTimeLimitStart(), policy.getTimeLimitEnd())) {
                throw new GatewayException(GatewayErrorCode.TIME_NOT_ALLOWED,
                        "当前时间不在允许访问时间段内 (" +
                        policy.getTimeLimitStart() + " - " + policy.getTimeLimitEnd() + ")");
            }
        }

        return SecurityPolicyDecision.builder()
                .allowed(true)
                .reason("Policy passed")
                .requiredPermission(policy.getRequiredPermission())
                .build();
    }

    private boolean isIpAllowed(String clientIp, String ipWhiteListJson) {
        try {
            @SuppressWarnings("unchecked")
            List<String> allowedIps = objectMapper.readValue(ipWhiteListJson, List.class);
            return allowedIps.contains(clientIp);
        } catch (Exception e) {
            log.warn("Failed to parse IP whitelist: {}", ipWhiteListJson, e);
            return false;
        }
    }

    private boolean isWithinTimeRange(String startTime, String endTime) {
        if (startTime == null || startTime.isBlank() || endTime == null || endTime.isBlank()) {
            return true;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime start = LocalTime.parse(startTime, formatter);
            LocalTime end = LocalTime.parse(endTime, formatter);
            LocalTime now = LocalTime.now();

            if (start.isBefore(end)) {
                return !now.isBefore(start) && !now.isAfter(end);
            } else {
                // Overnight range, e.g. 22:00 - 06:00
                return !now.isBefore(start) || !now.isAfter(end);
            }
        } catch (Exception e) {
            log.warn("Failed to parse time range: {}-{}", startTime, endTime, e);
            return true; // Fail open if time config is broken
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}