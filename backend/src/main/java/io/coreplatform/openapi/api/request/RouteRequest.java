package io.coreplatform.openapi.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RouteRequest {

    @NotNull(message = "所属API接口ID不能为空")
    private Long apiId;

    @NotBlank(message = "目标服务名不能为空")
    private String serviceName;

    @NotBlank(message = "后端目标地址不能为空")
    private String targetUrl;

    private Integer timeout;
    private String status;
}