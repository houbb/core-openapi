package io.coreplatform.openapi.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DefinitionRequest {

    @NotNull(message = "所属服务ID不能为空")
    private Long serviceId;

    @NotBlank(message = "接口名称不能为空")
    private String name;

    @NotBlank(message = "请求路径不能为空")
    private String path;

    @NotBlank(message = "HTTP方法不能为空")
    private String httpMethod;

    private String description;
    private String category;
}
