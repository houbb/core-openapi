package io.coreplatform.openapi.enterprise.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("enterprise_billing")
public class BillingAccountEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long organizationId;
    private String accountName;
    private BigDecimal balance;
    private String currency;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String createUser;
    private String updateUser;
}