package io.coreplatform.openapi.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("api_security_policy")
public class ApiSecurityPolicyEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long apiId;
    private String requiredPermission;
    private Integer authRequired;
    private Integer signRequired;
    private String ipWhiteList;
    private Integer timeLimitEnabled;
    private String timeLimitStart;
    private String timeLimitEnd;
    private Integer tenantCheck;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}