package io.coreplatform.openapi.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("openapi_sdk_project")
public class SdkProjectEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String language;
    private String version;
    private String status;
    private String errorMessage;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
