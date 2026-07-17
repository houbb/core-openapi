package io.coreplatform.openapi.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateResponseCommand {

    @NotNull(message = "接口ID不能为空")
    private Long apiId;

    @NotBlank(message = "HTTP状态码不能为空")
    private String statusCode;

    private String contentType;
    private String schema;
    private String example;
    private String description;
}