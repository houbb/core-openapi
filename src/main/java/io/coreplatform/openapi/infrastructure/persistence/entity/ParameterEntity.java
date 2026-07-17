package io.coreplatform.openapi.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("openapi_parameter")
public class ParameterEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long apiId;
    private String name;
    private String location;
    private String type;
    private Integer required;
    private String description;
    private String example;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
