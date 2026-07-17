package io.coreplatform.openapi.rate.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("rate_limit_policy")
public class RateLimitPolicyEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String scope;
    private Long scopeId;
    private String algorithm;
    private Integer limitValue;
    private Integer refillRate;
    private Integer refillPeriod;
    private String status;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}