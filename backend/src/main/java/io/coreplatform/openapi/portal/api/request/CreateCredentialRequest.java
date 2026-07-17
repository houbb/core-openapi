package io.coreplatform.openapi.portal.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCredentialRequest {
    @NotBlank(message = "名称不能为空")
    private String name;
    private String environment;
    private String expireDays;
}