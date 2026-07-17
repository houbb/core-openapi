package io.coreplatform.openapi.portal.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaygroundResponse {
    private int statusCode;
    private Map<String, String> responseHeaders;
    private String body;
    private long latencyMs;
}