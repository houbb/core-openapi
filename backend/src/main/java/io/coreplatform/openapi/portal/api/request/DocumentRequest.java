package io.coreplatform.openapi.portal.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DocumentRequest {
    @NotBlank(message = "标题不能为空")
    private String title;
    @NotBlank(message = "Slug不能为空")
    private String slug;
    @NotBlank(message = "分类不能为空")
    private String category;
    @NotBlank(message = "内容不能为空")
    private String content;
    private Integer sortOrder;
    private String status;
}