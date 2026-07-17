package io.coreplatform.openapi.gateway;

import io.coreplatform.openapi.api.exception.BusinessException;

public class GatewayException extends BusinessException {

    private final GatewayErrorCode gatewayErrorCode;

    public GatewayException(GatewayErrorCode gatewayErrorCode) {
        super(mapToHttpStatus(gatewayErrorCode), String.valueOf(gatewayErrorCode.getCode()), gatewayErrorCode.getMessage());
        this.gatewayErrorCode = gatewayErrorCode;
    }

    public GatewayErrorCode getGatewayErrorCode() {
        return gatewayErrorCode;
    }

    /**
     * Map gateway error code to HTTP status.
     * - Route not found → 404
     * - Invalid request → 400
     * - Service errors → 502 Bad Gateway
     * - Internal errors → 500
     */
    private static int mapToHttpStatus(GatewayErrorCode errorCode) {
        return switch (errorCode) {
            case ROUTE_NOT_FOUND -> 404;
            case INVALID_REQUEST -> 400;
            case SERVICE_UNAVAILABLE, SERVICE_TIMEOUT, BACKEND_ERROR -> 502;
            case GATEWAY_INTERNAL_ERROR -> 500;
            default -> 500;
        };
    }
}