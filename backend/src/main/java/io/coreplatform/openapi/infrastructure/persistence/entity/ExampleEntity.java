package io.coreplatform.openapi.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("openapi_example")
public class ExampleEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long apiId;
    private String type;
    private String name;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
