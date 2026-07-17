package io.coreplatform.openapi.security.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationRole {
    private Long id;
    private Long applicationId;
    private Long roleId;
    private LocalDateTime createTime;
}