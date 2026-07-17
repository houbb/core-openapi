package io.coreplatform.openapi.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateParameterCommand {

    @NotNull(message = "接口ID不能为空")
    private Long apiId;

    @NotBlank(message = "参数名不能为空")
    private String name;

    @NotBlank(message = "参数位置不能为空")
    private String location;

    @NotBlank(message = "参数类型不能为空")
    private String type;

    private Boolean required;
    private String description;
    private String example;
    private Integer sortOrder;
}