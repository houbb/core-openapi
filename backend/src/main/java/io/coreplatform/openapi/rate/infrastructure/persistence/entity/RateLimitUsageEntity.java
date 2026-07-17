package io.coreplatform.openapi.rate.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("rate_limit_usage")
public class RateLimitUsageEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long policyId;
    private Long apiId;
    private Long applicationId;
    private String identityId;
    private Integer requestCount;
    private Integer blockedCount;
    private LocalDateTime recordedAt;
    private LocalDateTime createTime;
}