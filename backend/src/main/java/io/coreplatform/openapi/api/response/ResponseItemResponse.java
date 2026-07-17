package io.coreplatform.openapi.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseItemResponse {
    private Long id;
    private Long apiId;
    private String statusCode;
    private String contentType;
    private String schema;
    private String example;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}