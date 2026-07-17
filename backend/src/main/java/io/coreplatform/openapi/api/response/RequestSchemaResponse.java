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
public class RequestSchemaResponse {
    private Long id;
    private Long apiId;
    private String contentType;
    private String schemaJson;
    private String exampleJson;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}