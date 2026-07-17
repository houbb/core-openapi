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
public class RouteResponse {
    private Long id;
    private Long apiId;
    private String serviceName;
    private String targetUrl;
    private Integer timeout;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}