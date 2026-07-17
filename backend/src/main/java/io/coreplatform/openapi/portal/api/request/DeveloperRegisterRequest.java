package io.coreplatform.openapi.portal.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeveloperRegisterRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @Email(message = "邮箱格式不正确")
    private String email;
    @NotBlank(message = "密码不能为空")
    private String password;
    private String developerType;
    private String companyName;
    private String phone;
}