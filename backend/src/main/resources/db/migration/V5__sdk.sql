-- ============================================================
-- core-openapi: SDK Runtime — Phase 4
-- Tables: openapi_sdk_project, openapi_sdk_generation
-- ============================================================

-- 1. SDK Project — one per SDK generation request
CREATE TABLE IF NOT EXISTS openapi_sdk_project (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    name          VARCHAR(100)  NOT NULL COMMENT 'SDK名称',
    language      VARCHAR(20)   NOT NULL COMMENT '语言: java|python|javascript',
    version       VARCHAR(20)   NOT NULL DEFAULT '1.0.0' COMMENT 'SDK版本号',
    status        VARCHAR(20)   NOT NULL DEFAULT 'GENERATING' COMMENT '状态: GENERATING|READY|FAILED',
    error_message TEXT          COMMENT '失败原因',
    create_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);

-- 2. SDK Generation — each re-generation creates a new record
CREATE TABLE IF NOT EXISTS openapi_sdk_generation (
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    sdk_project_id    BIGINT        NOT NULL COMMENT '所属SDK项目ID',
    api_ids           TEXT          COMMENT '关联的API ID列表(JSON数组)',
    api_version       VARCHAR(20)   COMMENT '生成时的API版本',
    generator_version VARCHAR(20)   COMMENT '生成器版本',
    status            VARCHAR(20)   NOT NULL DEFAULT 'GENERATING' COMMENT '状态: GENERATING|READY|FAILED',
    download_url      VARCHAR(500)  COMMENT '下载相对路径',
    file_size         BIGINT        DEFAULT 0 COMMENT '文件大小(bytes)',
    error_message     TEXT          COMMENT '失败原因',
    create_time       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);
CREATE INDEX IF NOT EXISTS idx_gen_project ON openapi_sdk_generation(sdk_project_id);
CREATE INDEX IF NOT EXISTS idx_gen_status ON openapi_sdk_generation(status);
