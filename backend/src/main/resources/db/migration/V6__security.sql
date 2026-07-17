-- ============================================================
-- core-openapi: Security Runtime — Phase 5
-- Tables: security_role, role_permission, application_role,
--         api_security_policy, security_audit_log, security_risk_event
-- ALTER:  add tenant_id to openapi_user, openapi_application, openapi_api_key
-- ============================================================

-- ============================================================
-- 0. Extend existing tables with tenant_id for tenant isolation
-- ============================================================
ALTER TABLE openapi_user ADD COLUMN tenant_id VARCHAR(64) DEFAULT '' COMMENT '租户ID';
ALTER TABLE openapi_application ADD COLUMN tenant_id VARCHAR(64) DEFAULT '' COMMENT '租户ID';
ALTER TABLE openapi_api_key ADD COLUMN tenant_id VARCHAR(64) DEFAULT '' COMMENT '租户ID';

CREATE INDEX IF NOT EXISTS idx_user_tenant ON openapi_user(tenant_id);
CREATE INDEX IF NOT EXISTS idx_app_tenant ON openapi_application(tenant_id);
CREATE INDEX IF NOT EXISTS idx_key_tenant ON openapi_api_key(tenant_id);

-- ============================================================
-- 1. Security Role
-- ============================================================
CREATE TABLE IF NOT EXISTS security_role (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL COMMENT '角色名称，如 AI_USER, ADMIN, READ_ONLY',
    description VARCHAR(500) DEFAULT '' COMMENT '角色描述',
    tenant_id   VARCHAR(64)  DEFAULT '' COMMENT '租户ID，空=全局角色',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_role_name_tenant ON security_role(name, tenant_id);

-- ============================================================
-- 2. Role-Permission mapping
-- ============================================================
CREATE TABLE IF NOT EXISTS role_permission (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id       BIGINT NOT NULL COMMENT 'FK to security_role.id',
    permission_id BIGINT NOT NULL COMMENT 'FK to api_permission.id',
    create_time   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_role_perm ON role_permission(role_id, permission_id);

-- ============================================================
-- 3. Application-Role mapping
-- ============================================================
CREATE TABLE IF NOT EXISTS application_role (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    application_id BIGINT NOT NULL COMMENT 'FK to openapi_application.id',
    role_id        BIGINT NOT NULL COMMENT 'FK to security_role.id',
    create_time    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_app_role ON application_role(application_id, role_id);

-- ============================================================
-- 4. API Security Policy (permission binding + policy rules)
-- ============================================================
CREATE TABLE IF NOT EXISTS api_security_policy (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    api_id              BIGINT       NOT NULL COMMENT 'FK to openapi_definition.id',
    required_permission VARCHAR(100) DEFAULT '' COMMENT '访问此API需要的权限code, e.g. ai.chat',
    auth_required       TINYINT      DEFAULT 1 COMMENT '是否需要认证: 0-否 1-是',
    sign_required       TINYINT      DEFAULT 0 COMMENT '是否需要签名: 0-否 1-是',
    ip_white_list       TEXT         COMMENT 'IP白名单，JSON数组，空=不限制',
    time_limit_enabled  TINYINT      DEFAULT 0 COMMENT '是否启用时间限制: 0-否 1-是',
    time_limit_start    VARCHAR(8)   DEFAULT '' COMMENT '允许访问开始时间 HH:mm',
    time_limit_end      VARCHAR(8)   DEFAULT '' COMMENT '允许访问结束时间 HH:mm',
    tenant_check        TINYINT      DEFAULT 0 COMMENT '是否需要租户检查: 0-否 1-是',
    status              VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE|INACTIVE',
    create_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_api_policy ON api_security_policy(api_id);

-- ============================================================
-- 5. Security Audit Log
-- ============================================================
CREATE TABLE IF NOT EXISTS security_audit_log (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_type      VARCHAR(50)  NOT NULL COMMENT '事件类型: AUTH_SUCCESS|AUTH_FAILURE|PERMISSION_DENIED|POLICY_DENIED|RISK_ALERT|KEY_REVOKED|KEY_CREATED|ROLE_ASSIGNED|API_CALL',
    identity_type   VARCHAR(30)  DEFAULT '' COMMENT '身份类型: API_KEY|JWT',
    identity_id     VARCHAR(100) DEFAULT '' COMMENT '身份标识: applicationCode 或 JWT subject',
    resource_type   VARCHAR(50)  DEFAULT '' COMMENT '资源类型: API|ROLE|PERMISSION|APPLICATION',
    resource_id     VARCHAR(100) DEFAULT '' COMMENT '资源标识',
    result          VARCHAR(20)  NOT NULL DEFAULT 'SUCCESS' COMMENT '结果: SUCCESS|FAILURE|DENIED',
    detail          TEXT         COMMENT '详细信息（JSON）',
    ip              VARCHAR(50)  DEFAULT '' COMMENT '请求IP',
    request_id      VARCHAR(64)  DEFAULT '' COMMENT '关联请求ID',
    tenant_id       VARCHAR(64)  DEFAULT '' COMMENT '租户ID',
    created_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_audit_event_type ON security_audit_log(event_type);
CREATE INDEX IF NOT EXISTS idx_audit_identity ON security_audit_log(identity_id);
CREATE INDEX IF NOT EXISTS idx_audit_time ON security_audit_log(created_time);
CREATE INDEX IF NOT EXISTS idx_audit_tenant ON security_audit_log(tenant_id);

-- ============================================================
-- 6. Security Risk Event
-- ============================================================
CREATE TABLE IF NOT EXISTS security_risk_event (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    identity_id     VARCHAR(100) DEFAULT '' COMMENT '身份标识',
    risk_type       VARCHAR(50)  NOT NULL COMMENT '风险类型: RATE_ANOMALY|IP_ANOMALY|FREQUENCY_SPIKE',
    severity        VARCHAR(20)  NOT NULL DEFAULT 'WARNING' COMMENT '严重程度: WARNING|CRITICAL|BLOCK',
    detail          TEXT         COMMENT '详细信息（JSON）',
    request_count   BIGINT       DEFAULT 0 COMMENT '触发时的请求数',
    window_seconds  INT          DEFAULT 0 COMMENT '检测窗口（秒）',
    threshold_count BIGINT       DEFAULT 0 COMMENT '阈值',
    tenant_id       VARCHAR(64)  DEFAULT '' COMMENT '租户ID',
    created_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_risk_identity ON security_risk_event(identity_id);
CREATE INDEX IF NOT EXISTS idx_risk_time ON security_risk_event(created_time);

-- ============================================================
-- 7. Seed Data
-- ============================================================

-- Default roles
INSERT INTO security_role (name, description, tenant_id) VALUES ('ADMIN', '管理员，拥有所有权限', '');
INSERT INTO security_role (name, description, tenant_id) VALUES ('AI_USER', 'AI功能用户', '');
INSERT INTO security_role (name, description, tenant_id) VALUES ('READ_ONLY', '只读用户', '');

-- Ensure base permissions exist (INSERT OR IGNORE for idempotency)
INSERT OR IGNORE INTO api_permission (name, description) VALUES ('admin.all', '管理员全部权限');
INSERT OR IGNORE INTO api_permission (name, description) VALUES ('ai.chat', 'AI对话接口权限');
INSERT OR IGNORE INTO api_permission (name, description) VALUES ('ai.embedding', 'AI向量嵌入接口权限');
INSERT OR IGNORE INTO api_permission (name, description) VALUES ('storage.read', '存储读取权限');
INSERT OR IGNORE INTO api_permission (name, description) VALUES ('storage.upload', '存储上传权限');
INSERT OR IGNORE INTO api_permission (name, description) VALUES ('user.read', '用户读取权限');