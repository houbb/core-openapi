package io.coreplatform.openapi.portal.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class PlaygroundRequest {
    @NotBlank(message = "API Key不能为空")
    private String apiKey;
    @NotBlank(message = "Method不能为空")
    private String method;
    @NotBlank(message = "URL不能为空")
    private String url;
    private Map<String, String> headers;
    private String body;
}