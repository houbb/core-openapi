package io.coreplatform.openapi.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("openapi_application")
public class ApplicationEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String appName;
    private String appCode;
    private Long ownerId;
    private String description;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
