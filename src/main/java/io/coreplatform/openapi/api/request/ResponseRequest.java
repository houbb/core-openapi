package io.coreplatform.openapi.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResponseRequest {

    @NotBlank(message = "HTTP状态码不能为空")
    private String statusCode;

    private String contentType;
    private String schema;
    private String example;
}
