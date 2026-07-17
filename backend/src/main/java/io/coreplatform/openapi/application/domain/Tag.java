package io.coreplatform.openapi.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
    private Long id;
    private String name;
    private String color;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}