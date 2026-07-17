package io.coreplatform.openapi.gateway;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnifiedResponse<T> {
    private int code;
    private String message;
    private T data;
    private String traceId;

    public static <T> UnifiedResponse<T> success(T data, String traceId) {
        return UnifiedResponse.<T>builder()
                .code(0)
                .message("success")
                .data(data)
                .traceId(traceId)
                .build();
    }

    public static <T> UnifiedResponse<T> error(int code, String message, String traceId) {
        return UnifiedResponse.<T>builder()
                .code(code)
                .message(message)
                .traceId(traceId)
                .build();
    }
}