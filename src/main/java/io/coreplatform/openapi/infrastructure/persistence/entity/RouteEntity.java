package io.coreplatform.openapi.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("openapi_route")
public class RouteEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long apiId;
    private String serviceName;
    private String targetUrl;
    private Integer timeout;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}