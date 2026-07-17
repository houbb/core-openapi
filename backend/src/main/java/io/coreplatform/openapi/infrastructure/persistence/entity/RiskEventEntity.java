package io.coreplatform.openapi.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("security_risk_event")
public class RiskEventEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String identityId;
    private String riskType;
    private String severity;
    private String detail;
    private Long requestCount;
    private Integer windowSeconds;
    private Long thresholdCount;
    private String tenantId;
    private LocalDateTime createdTime;
}