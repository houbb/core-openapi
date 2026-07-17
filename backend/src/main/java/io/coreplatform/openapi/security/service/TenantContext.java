package io.coreplatform.openapi.security.service;

import io.coreplatform.openapi.gateway.RequestContext;
import io.coreplatform.openapi.gateway.RequestContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TenantContext {

    /**
     * Resolve tenant from the current request context.
     * Priority: RequestContext (set during auth) > X-Tenant-Id header
     *
     * @return the tenant ID or empty string if not set
     */
    public static String getCurrentTenant() {
        RequestContext ctx = RequestContextHolder.current();
        if (ctx != null && ctx.getTenantId() != null) {
            return ctx.getTenantId();
        }
        return "";
    }

    /**
     * Set the tenant ID in the current request context.
     */
    public static void setCurrentTenant(String tenantId) {
        RequestContext ctx = RequestContextHolder.current();
        if (ctx != null) {
            ctx.setTenantId(tenantId);
        }
    }
}