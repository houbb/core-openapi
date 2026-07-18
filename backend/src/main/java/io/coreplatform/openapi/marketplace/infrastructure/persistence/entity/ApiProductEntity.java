package io.coreplatform.openapi.marketplace.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("api_product")
public class ApiProductEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private Long providerId;
    private Long apiId;
    private String category;
    private String iconUrl;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
