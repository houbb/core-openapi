package io.coreplatform.openapi.enterprise.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IdentityProviderRequest {
    @NotBlank(message = "Provider名称不能为空")
    private String name;

    @NotBlank(message = "Provider类型不能为空")
    private String providerType;

    private String configJson;

    private Integer isDefault;

    private String status;
}