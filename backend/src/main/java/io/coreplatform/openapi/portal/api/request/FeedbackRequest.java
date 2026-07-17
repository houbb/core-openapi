package io.coreplatform.openapi.portal.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FeedbackRequest {
    private String type;
    @NotBlank(message = "标题不能为空")
    private String title;
    private String content;
}