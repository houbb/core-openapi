package io.coreplatform.openapi.application.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateApplicationCommand {

    @NotBlank(message = "应用名称不能为空")
    private String appName;

    @NotBlank(message = "应用编码不能为空")
    private String appCode;

    private Long ownerId;
    private String description;
    private String status;
}
