package io.coreplatform.openapi.portal.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("api_subscription")
public class ApiSubscriptionEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long apiId;
    private String plan;
    private String status;
    private Integer maxQps;
    private Integer maxDaily;
    private LocalDateTime subscribedAt;
    private LocalDateTime expiredAt;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
