package io.coreplatform.openapi.enterprise.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerResponse {
    private Long id;
    private Long organizationId;
    private String name;
    private String level;
    private String status;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}