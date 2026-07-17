package io.coreplatform.openapi.portal.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {
    private Long id;
    private Long apiId;
    private String apiName;
    private String plan;
    private String status;
    private Integer maxQps;
    private Integer maxDaily;
    private LocalDateTime subscribedAt;
    private LocalDateTime expiredAt;
}