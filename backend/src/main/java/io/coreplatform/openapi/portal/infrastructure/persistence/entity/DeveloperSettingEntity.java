package io.coreplatform.openapi.portal.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("developer_setting")
public class DeveloperSettingEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String language;
    private String theme;
    private Integer notifyEmail;
    private Integer notifyUsage;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
