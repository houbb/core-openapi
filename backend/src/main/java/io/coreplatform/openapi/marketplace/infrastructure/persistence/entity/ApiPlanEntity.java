package io.coreplatform.openapi.marketplace.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("api_plan")
public class ApiPlanEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    private String billingType;
    private String limitConfig;
    private String status;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
