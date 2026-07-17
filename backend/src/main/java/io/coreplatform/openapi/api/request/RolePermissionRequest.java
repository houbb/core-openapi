package io.coreplatform.openapi.api.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RolePermissionRequest {
    @NotNull(message = "权限ID不能为空")
    private Long permissionId;
}