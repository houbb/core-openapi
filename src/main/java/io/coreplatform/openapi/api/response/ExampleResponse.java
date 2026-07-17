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
public class ExampleResponse {
    private Long id;
    private Long apiId;
    private String type;
    private String name;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}