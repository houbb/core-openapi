package io.coreplatform.openapi.portal.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiSubscription {
    private Long id;
    private Long userId;
    private Long apiId;
    private String plan;
    private String status;
    private Integer maxQps;
    private Integer maxDaily;
    private LocalDateTime subscribedAt;
    private LocalDateTime expiredAt;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
