package io.coreplatform.openapi.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("openapi_request_schema")
public class RequestSchemaEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long apiId;
    private String contentType;
    private String schemaJson;
    private String exampleJson;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
