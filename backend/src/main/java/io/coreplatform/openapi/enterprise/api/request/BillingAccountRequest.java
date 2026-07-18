package io.coreplatform.openapi.enterprise.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BillingAccountRequest {
    @NotBlank(message = "账户名称不能为空")
    private String accountName;

    private String currency;
}