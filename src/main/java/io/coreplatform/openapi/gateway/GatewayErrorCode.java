package io.coreplatform.openapi.gateway;

public enum GatewayErrorCode {
    SUCCESS(0, "success"),
    ROUTE_NOT_FOUND(40400, "未找到匹配的路由"),
    INVALID_REQUEST(40000, "无效的请求"),
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