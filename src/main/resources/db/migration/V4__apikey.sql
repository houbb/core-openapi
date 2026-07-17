-- ============================================================
-- core-openapi: API Key Runtime — Phase 3
-- Tables: openapi_user, openapi_application, openapi_api_key,
--         api_permission, application_permission, api_key_usage_log
-- ============================================================

-- 1. User
CREATE TABLE IF NOT EXISTS openapi_user (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(100) NOT NULL,
    email       VARCHAR(200) DEFAULT '',
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE|INACTIVE',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_user_username ON openapi_user(username);

-- 2. Application
CREATE TABLE IF NOT EXISTS openapi_application (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    app_name    VARCHAR(100) NOT NULL COMMENT '应用名称',
    app_code    VARCHAR(100) NOT NULL COMMENT '唯一编码',
    owner_id    BIGINT       COMMENT 'FK to openapi_user.id',
    description TEXT         DEFAULT '' COMMENT '描述',
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE|INACTIVE',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_app_code ON openapi_application(app_code);
CREATE INDEX IF NOT EXISTS idx_app_owner ON openapi_application(owner_id);

-- 3. API Key (stores hash, never plaintext)
CREATE TABLE IF NOT EXISTS openapi_api_key (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    application_id  BIGINT       NOT NULL COMMENT 'FK to openapi_application.id',
    key_prefix      VARCHAR(20)  NOT NULL COMMENT 'sk_live_ or sk_test_ for display',
    key_hash        VARCHAR(64)  NOT NULL COMMENT 'SHA-256 hash of full key',
    name            VARCHAR(100) DEFAULT '' COMMENT 'Key label',
    environment     VARCHAR(10)  NOT NULL DEFAULT 'LIVE' COMMENT 'LIVE|TEST',
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE|DISABLED|EXPIRED|REVOKED',
    expire_time     DATETIME     COMMENT 'Expiration time, NULL = never',
    last_used_time  DATETIME     COMMENT 'Last time the key was used',
    created_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_key_hash ON openapi_api_key(key_hash);
CREATE INDEX IF NOT EXISTS idx_key_app ON openapi_api_key(application_id);

-- 4. Permission
CREATE TABLE IF NOT EXISTS api_permission (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL COMMENT 'e.g. ai.chat, storage.read',
    description VARCHAR(500) DEFAULT '' COMMENT '描述'
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_perm_name ON api_permission(name);

-- 5. Application-Permission mapping
CREATE TABLE IF NOT EXISTS application_permission (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    application_id  BIGINT NOT NULL,
    permission_id   BIGINT NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_app_perm ON application_permission(application_id, permission_id);

-- 6. API Key Usage Log
CREATE TABLE IF NOT EXISTS api_key_usage_log (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    api_key_id  BIGINT       NOT NULL,
    api_id      BIGINT       COMMENT 'FK to openapi_definition.id',
    request_id  VARCHAR(64)  DEFAULT '',
    ip          VARCHAR(50)  DEFAULT '',
    timestamp   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status      VARCHAR(20)  NOT NULL DEFAULT 'SUCCESS' COMMENT 'SUCCESS|FAILED'
);
CREATE INDEX IF NOT EXISTS idx_usage_key ON api_key_usage_log(api_key_id);
CREATE INDEX IF NOT EXISTS idx_usage_time ON api_key_usage_log(timestamp);

-- Seed some default permissions
INSERT INTO api_permission (name, description) VALUES ('ai.chat', 'AI对话接口权限');
INSERT INTO api_permission (name, description) VALUES ('ai.embedding', 'AI向量嵌入接口权限');
INSERT INTO api_permission (name, description) VALUES ('storage.read', '存储读取权限');
INSERT INTO api_permission (name, description) VALUES ('storage.upload', '存储上传权限');
INSERT INTO api_permission (name, description) VALUES ('user.read', '用户读取权限');
