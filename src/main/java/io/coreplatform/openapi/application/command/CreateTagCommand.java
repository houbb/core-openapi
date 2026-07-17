package io.coreplatform.openapi.application.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTagCommand {

    @NotBlank(message = "标签名不能为空")
    private String name;

    private String color;
}
