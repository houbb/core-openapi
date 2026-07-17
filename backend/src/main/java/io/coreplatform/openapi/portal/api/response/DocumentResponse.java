package io.coreplatform.openapi.portal.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
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