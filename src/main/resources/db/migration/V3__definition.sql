-- ============================================================
-- core-openapi: API Definition Runtime — Phase 2
-- Tables: openapi_request_schema, openapi_example
-- Alter:  openapi_response (add description column)
-- ============================================================

-- 1. Request Schema — JSON Schema for request body
CREATE TABLE IF NOT EXISTS openapi_request_schema (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    api_id       BIGINT        NOT NULL COMMENT 'FK to openapi_definition.id',
    content_type VARCHAR(50)   DEFAULT 'application/json' COMMENT '内容类型',
    schema_json  TEXT          COMMENT 'JSON Schema定义',
    example_json TEXT          COMMENT '示例JSON',
    description  VARCHAR(500)  DEFAULT '' COMMENT '描述',
    create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_request_schema_api ON openapi_request_schema(api_id);

-- 2. API Example — request/response examples
CREATE TABLE IF NOT EXISTS openapi_example (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    api_id      BIGINT        NOT NULL COMMENT 'FK to openapi_definition.id',
    type        VARCHAR(10)   NOT NULL COMMENT '类型: REQUEST|RESPONSE',
    name        VARCHAR(100)  DEFAULT '' COMMENT '示例名称',
    content     TEXT          COMMENT '示例内容',
    create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);
CREATE INDEX IF NOT EXISTS idx_example_api ON openapi_example(api_id);

-- 3. Extend openapi_response with description
ALTER TABLE openapi_response ADD COLUMN description VARCHAR(500) DEFAULT '' COMMENT '响应描述';
