package io.coreplatform.openapi.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleRequest {
    @NotBlank(message = "角色名称不能为空")
    private String name;
    private String description;
    private String tenantId;
}