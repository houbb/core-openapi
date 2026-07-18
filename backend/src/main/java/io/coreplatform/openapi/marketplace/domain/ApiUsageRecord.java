package io.coreplatform.openapi.marketplace.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiUsageRecord {
    private Long id;
    private Long productId;
    private Long apiId;
    private Long userId;
    private Long requestCount;
    private Long tokenCount;
    private Long errorCount;
    private LocalDate recordedDate;
    private LocalDateTime createTime;
}
