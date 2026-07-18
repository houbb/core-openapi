package io.coreplatform.openapi.enterprise.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("enterprise_contract")
public class ContractEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long organizationId;
    private String contractNo;
    private String name;
    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Long maxRequests;
    private Integer maxQps;
    private Integer supportsPhone;
    private Integer supports724;
    private String description;
    private String tenantId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String createUser;
    private String updateUser;
}