package io.coreplatform.openapi.marketplace.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("api_provider")
public class ApiProviderEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String type;
    private Long ownerId;
    private Integer verified;
    private String status;
    private String contactEmail;
    private String website;
    private String logoUrl;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
