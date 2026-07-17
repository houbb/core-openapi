package io.coreplatform.openapi.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateVersionCommand {

    @NotNull(message = "接口ID不能为空")
    private Long apiId;

    @NotBlank(message = "版本号不能为空")
    private String version;

    private String changelog;
}
