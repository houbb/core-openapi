package io.coreplatform.openapi.api.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationRoleRequest {
    @NotNull(message = "角色ID不能为空")
    private Long roleId;
}