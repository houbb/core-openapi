package io.coreplatform.openapi.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private String type;
    private String title;
    private int status;
    private String detail;
    private String errorCode;
    private String traceId;
    private LocalDateTime timestamp;
}
