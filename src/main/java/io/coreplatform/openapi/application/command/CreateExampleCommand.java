package io.coreplatform.openapi.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateExampleCommand {

    @NotNull(message = "接口ID不能为空")
    private Long apiId;

    @NotBlank(message = "示例类型不能为空")
    private String type;      // REQUEST | RESPONSE

    private String name;
    private String content;
}
