package io.coreplatform.openapi.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateApiKeyRequest {

    @NotNull(message = "应用ID不能为空")
    private Long applicationId;

    @NotBlank(message = "环境不能为空")
    private String environment;

    private String name;

    private String expireTime;
}