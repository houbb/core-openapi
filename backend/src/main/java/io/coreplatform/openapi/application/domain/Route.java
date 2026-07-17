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
public class Route {
    private Long id;
    private Long apiId;
    private String serviceName;
    private String targetUrl;
    private Integer timeout;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}