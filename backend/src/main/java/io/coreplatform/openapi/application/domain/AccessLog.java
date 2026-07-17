package io.coreplatform.openapi.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessLog {
    private Long id;
    private String requestId;
    private Long apiId;
    private String clientId;
    private String requestMethod;
    private String requestPath;
    private String targetUrl;
    private LocalDateTime requestTime;
    private LocalDateTime responseTime;
    private Integer statusCode;
    private Long costTime;
    private String errorMessage;
    private LocalDateTime createTime;
}