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
public class ParameterResponse {
    private Long id;
    private Long apiId;
    private String name;
    private String location;
    private String type;
    private Boolean required;
    private String description;
    private String example;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}