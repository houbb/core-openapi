package io.coreplatform.openapi.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("openapi_service")
public class ServiceEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String serviceName;
    private String serviceCode;
    private String description;
    private String owner;
    private String status;
    private String version;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String createUser;
    private String updateUser;
}
