package io.coreplatform.openapi.application.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateServiceCommand {

    @NotBlank(message = "服务名称不能为空")
    private String serviceName;

    @NotBlank(message = "服务编码不能为空")
    private String serviceCode;

    private String description;
    private String owner;
    private String version;
}