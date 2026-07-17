package io.coreplatform.openapi.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApplicationRequest {

    @NotBlank(message = "应用名称不能为空")
    private String appName;

    @NotBlank(message = "应用编码不能为空")
    private String appCode;

    private Long ownerId;
    private String description;
    private String status;
}