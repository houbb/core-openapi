-- ============================================================
-- core-openapi: API Gateway Runtime — Phase 1
-- Tables: openapi_route, openapi_access_log, openapi_gateway_config
-- ============================================================

-- 1. API Route — links API definition to backend target URL
CREATE TABLE IF NOT EXISTS openapi_route (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    api_id       BIGINT        NOT NULL COMMENT 'FK to openapi_definition.id',
    service_name VARCHAR(100)  NOT NULL COMMENT '目标服务名',
    target_url   VARCHAR(500)  NOT NULL COMMENT '后端目标地址: http://host:port',
    timeout      INT           DEFAULT 30000 COMMENT '超时时间(ms)',
    status       VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE|INACTIVE',
    create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_route_api ON openapi_route(api_id);
CREATE INDEX IF NOT EXISTS idx_route_status ON openapi_route(status);

-- 2. API Access Log — records every gateway request
CREATE TABLE IF NOT EXISTS openapi_access_log (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    request_id     VARCHAR(64)  NOT NULL COMMENT '请求唯一ID',
    api_id         BIGINT       COMMENT '关联API定义ID',
    client_id      VARCHAR(100) DEFAULT '' COMMENT '调用方标识',
    request_method VARCHAR(10)  NOT NULL COMMENT 'HTTP方法',
    request_path   VARCHAR(500) NOT NULL COMMENT '请求路径',
    target_url     VARCHAR(500) DEFAULT '' COMMENT '转发的后端地址',
    request_time   DATETIME     NOT NULL COMMENT '请求到达时间',
    response_time  DATETIME     COMMENT '响应返回时间',
    status_code    INT          NOT NULL DEFAULT 0 COMMENT 'HTTP状态码',
    cost_time      BIGINT       DEFAULT 0 COMMENT '耗时(ms)',
    error_message  TEXT         COMMENT '错误信息',
    create_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);
CREATE INDEX IF NOT EXISTS idx_log_request_id ON openapi_access_log(request_id);
CREATE INDEX IF NOT EXISTS idx_log_api_id ON openapi_access_log(api_id);
CREATE INDEX IF NOT EXISTS idx_log_request_time ON openapi_access_log(request_time);

-- 3. Gateway Config — KV config table
CREATE TABLE IF NOT EXISTS openapi_gateway_config (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key   VARCHAR(100)  NOT NULL COMMENT '配置键',
    config_value VARCHAR(500)  NOT NULL COMMENT '配置值',
    description  VARCHAR(500)  DEFAULT '' COMMENT '描述',
    create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_config_key ON openapi_gateway_config(config_key);