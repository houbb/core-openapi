package io.coreplatform.openapi.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExampleRequest {

    @NotBlank(message = "示例类型不能为空")
    private String type;      // REQUEST | RESPONSE

    private String name;
    private String content;
}