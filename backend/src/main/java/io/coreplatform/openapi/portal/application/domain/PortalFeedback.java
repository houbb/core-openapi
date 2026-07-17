package io.coreplatform.openapi.portal.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortalFeedback {
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