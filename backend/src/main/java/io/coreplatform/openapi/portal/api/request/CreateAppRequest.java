package io.coreplatform.openapi.portal.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAppRequest {
    @NotBlank(message = "应用名称不能为空")
    private String appName;
    private String description;
    private String environment;
}