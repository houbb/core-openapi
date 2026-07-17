package io.coreplatform.openapi.portal.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("portal_document")
public class PortalDocumentEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String slug;
    private String category;
    private String content;
    private Integer sortOrder;
    private String status;
    private String author;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
