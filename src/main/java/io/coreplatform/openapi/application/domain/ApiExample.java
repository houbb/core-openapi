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
public class ApiExample {
    private Long id;
    private Long apiId;
    private String type;      // REQUEST | RESPONSE
    private String name;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
