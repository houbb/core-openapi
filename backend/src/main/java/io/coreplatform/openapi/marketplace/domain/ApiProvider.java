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
public class ApiProvider {
    private Long id;
    private String name;
    private String description;
    private String type;
    private Long ownerId;
    private Integer verified;
    private String status;
    private String contactEmail;
    private String website;
    private String logoUrl;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
