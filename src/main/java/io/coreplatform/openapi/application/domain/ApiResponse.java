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
public class ApiResponse {
    private Long id;
    private Long apiId;
    private String statusCode;
    private String contentType;
    private String schema;
    private String example;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
