package io.coreplatform.openapi.enterprise.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("enterprise_compliance")
public class CompliancePolicyEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long organizationId;
    private String name;
    private String policyType;
    private String configJson;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String createUser;
    private String updateUser;
}