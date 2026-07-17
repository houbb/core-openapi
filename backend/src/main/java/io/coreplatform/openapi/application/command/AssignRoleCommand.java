package io.coreplatform.openapi.application.command;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignRoleCommand {
    @NotNull(message = "角色ID不能为空")
    private Long roleId;
}