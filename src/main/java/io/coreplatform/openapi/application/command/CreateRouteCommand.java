package io.coreplatform.openapi.application.command;

import lombok.Data;

@Data
public class CreateRouteCommand {
    private Long apiId;
    private String serviceName;
    private String targetUrl;
    private Integer timeout;
    private String status;
}