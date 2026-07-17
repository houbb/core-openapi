package io.coreplatform.openapi.application.command;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignPermissionToRoleCommand {
    @NotNull(message = "权限ID不能为空")
    private Long permissionId;
}