package io.coreplatform.openapi.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("openapi_sdk_generation")
public class SdkGenerationEntity {

    @TableId(type = IdType.AUTO)
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