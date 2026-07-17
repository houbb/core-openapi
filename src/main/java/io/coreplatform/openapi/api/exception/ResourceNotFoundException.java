package io.coreplatform.openapi.api.exception;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resource, Long id) {
        super(404, "RESOURCE_NOT_FOUND", resource + " not found: " + id);
    }

    public ResourceNotFoundException(String message) {
        super(404, "RESOURCE_NOT_FOUND", message);
    }
}