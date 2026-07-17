package io.coreplatform.openapi.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("openapi_definition")
public class DefinitionEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long serviceId;
    private String name;
    private String path;
    private String httpMethod;
    private String description;
    private String category;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String createUser;
    private String updateUser;
}
