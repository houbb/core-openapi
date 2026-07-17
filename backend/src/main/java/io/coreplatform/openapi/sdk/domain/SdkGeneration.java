package io.coreplatform.openapi.sdk.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SdkGeneration {
    private Long id;
    private Long sdkProjectId;
    private String apiIds;
    private String apiVersion;
    private String generatorVersion;
    private String status;
    private String downloadUrl;
    private Long fileSize;
    private String errorMessage;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}