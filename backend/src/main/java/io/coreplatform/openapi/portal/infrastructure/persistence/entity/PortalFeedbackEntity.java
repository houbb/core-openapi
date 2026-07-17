package io.coreplatform.openapi.portal.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("portal_feedback")
public class PortalFeedbackEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String type;
    private String title;
    private String content;
    private String status;
    private String adminReply;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
