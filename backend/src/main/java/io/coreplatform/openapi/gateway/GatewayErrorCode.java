package io.coreplatform.openapi.gateway;

public enum GatewayErrorCode {
    SUCCESS(0, "success"),
    ROUTE_NOT_FOUND(40400, "未找到匹配的路由"),
    INVALID_REQUEST(40000, "无效的请求"),
    INVALID_API_KEY(40101, "无效的API Key"),
    API_KEY_EXPIRED(40102, "API Key已过期"),
    API_KEY_DISABLED(40103, "API Key已禁用"),
    PERMISSION_DENIED(40301, "无权限访问此API"),
    INVALID_JWT(40104, "无效的JWT令牌"),
    JWT_EXPIRED(40105, "JWT令牌已过期"),
    TENANT_REQUIRED(40302, "缺少租户信息"),
    IP_NOT_ALLOWED(40303, "IP不在白名单中"),
    TIME_NOT_ALLOWED(40304, "当前时间不在允许访问时间段内"),
    RISK_BLOCKED(42901, "触发风控规则，请求被拦截"),
    SERVICE_UNAVAILABLE(50001, "后端服务不可用"),
    SERVICE_TIMEOUT(50002, "后端服务超时"),
    BACKEND_ERROR(50003, "后端服务错误"),
    GATEWAY_INTERNAL_ERROR(50000, "网关内部错误");

    private final int code;
    private final String message;

    GatewayErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}