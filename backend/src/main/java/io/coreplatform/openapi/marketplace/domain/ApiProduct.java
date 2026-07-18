package io.coreplatform.openapi.marketplace.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiProduct {
    private Long id;
    private String name;
    private String description;
    private Long providerId;
    private Long apiId;
    private String category;
    private String iconUrl;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
