package io.coreplatform.openapi.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SdkGenerateRequest {

    @NotBlank(message = "SDK名称不能为空")
    private String name;

    @NotBlank(message = "语言不能为空")
    private String language;

    @NotBlank(message = "版本号不能为空")
    private String version;

    @NotEmpty(message = "至少选择一个API")
    private List<Long> apiIds;
}
