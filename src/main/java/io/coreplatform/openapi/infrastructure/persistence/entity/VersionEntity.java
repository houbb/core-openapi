package io.coreplatform.openapi.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("openapi_version")
public class VersionEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long apiId;
    private String version;
    private String status;
    private String changelog;
    private LocalDateTime releaseTime;
    private LocalDateTime deprecatedTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
