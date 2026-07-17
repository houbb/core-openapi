package io.coreplatform.openapi.application.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePermissionCommand {

    @NotBlank(message = "权限名称不能为空")
    private String name;

    private String description;
}
