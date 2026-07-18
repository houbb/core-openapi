package io.coreplatform.openapi.enterprise.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("enterprise_partner")
public class PartnerEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long organizationId;
    private String name;
    private String level;
    private String status;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private String description;
    private String tenantId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String createUser;
    private String updateUser;
}