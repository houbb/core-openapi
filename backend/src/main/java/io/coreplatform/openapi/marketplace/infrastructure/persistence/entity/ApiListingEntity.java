package io.coreplatform.openapi.marketplace.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("api_listing")
public class ApiListingEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private Integer featured;
    private Integer sortOrder;
    private String tags;
    private String highlightText;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
