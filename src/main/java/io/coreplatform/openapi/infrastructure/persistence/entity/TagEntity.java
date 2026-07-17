package io.coreplatform.openapi.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("openapi_tag")
public class TagEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String color;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
