package io.coreplatform.openapi.api.exception;

public class BusinessException extends RuntimeException {

    private final int status;
    private final String errorCode;

    public BusinessException(int status, String errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public BusinessException(String message) {
        this(400, "BUSINESS_ERROR", message);
    }

    public int getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
