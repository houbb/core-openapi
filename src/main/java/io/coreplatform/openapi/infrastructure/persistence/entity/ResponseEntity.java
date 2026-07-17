package io.coreplatform.openapi.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("openapi_response")
public class ResponseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long apiId;
    private String statusCode;
    private String contentType;
    private String schema;
    private String example;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}