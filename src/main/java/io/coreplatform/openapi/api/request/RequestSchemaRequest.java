package io.coreplatform.openapi.api.request;

import lombok.Data;

@Data
public class RequestSchemaRequest {

    private String contentType;
    private String schemaJson;
    private String exampleJson;
    private String description;
}