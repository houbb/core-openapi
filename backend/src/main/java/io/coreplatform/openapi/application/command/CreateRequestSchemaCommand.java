package io.coreplatform.openapi.application.command;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRequestSchemaCommand {

    @NotNull(message = "接口ID不能为空")
    private Long apiId;

    private String contentType;
    private String schemaJson;
    private String exampleJson;
    private String description;
}
