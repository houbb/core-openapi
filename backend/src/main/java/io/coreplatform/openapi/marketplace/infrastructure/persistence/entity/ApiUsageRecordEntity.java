package io.coreplatform.openapi.marketplace.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("api_usage_record")
public class ApiUsageRecordEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private Long apiId;
    private Long userId;
    private Long requestCount;
    private Long tokenCount;
    private Long errorCount;
    private LocalDate recordedDate;
    private LocalDateTime createTime;
}
