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
public class ApiListing {
    private Long id;
    private Long productId;
    private Integer featured;
    private Integer sortOrder;
    private String tags;
    private String highlightText;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
