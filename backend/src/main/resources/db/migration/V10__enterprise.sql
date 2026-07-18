-- ============================================================
-- Phase 9: Enterprise Open Platform
-- V10__enterprise.sql
-- ============================================================

-- ============================================================
-- 0. Extend existing tables with tenant_id for data isolation
-- ============================================================
ALTER TABLE openapi_definition ADD COLUMN tenant_id VARCHAR(64) DEFAULT '' COMMENT '租户ID';
ALTER TABLE openapi_service ADD COLUMN tenant_id VARCHAR(64) DEFAULT '' COMMENT '租户ID';
ALTER TABLE openapi_parameter ADD COLUMN tenant_id VARCHAR(64) DEFAULT '' COMMENT '租户ID';
ALTER TABLE openapi_response ADD COLUMN tenant_id VARCHAR(64) DEFAULT '' COMMENT '租户ID';
ALTER TABLE openapi_version ADD COLUMN tenant_id VARCHAR(64) DEFAULT '' COMMENT '租户ID';
ALTER TABLE openapi_tag ADD COLUMN tenant_id VARCHAR(64) DEFAULT '' COMMENT '租户ID';
ALTER TABLE api_key_usage_log ADD COLUMN tenant_id VARCHAR(64) DEFAULT '' COMMENT '租户ID';
ALTER TABLE api_permission ADD COLUMN tenant_id VARCHAR(64) DEFAULT '' COMMENT '租户ID';
ALTER TABLE api_subscription ADD COLUMN tenant_id VARCHAR(64) DEFAULT '' COMMENT '租户ID';

CREATE INDEX IF NOT EXISTS idx_definition_tenant ON openapi_definition(tenant_id);
CREATE INDEX IF NOT EXISTS idx_service_tenant ON openapi_service(tenant_id);
CREATE INDEX IF NOT EXISTS idx_version_tenant ON openapi_version(tenant_id);
CREATE INDEX IF NOT EXISTS idx_usage_tenant ON api_key_usage_log(tenant_id);

-- ============================================================
-- 0b. Extend openapi_definition with API Governance fields
-- ============================================================
ALTER TABLE openapi_definition ADD COLUMN lifecycle_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '生命周期: DRAFT|REVIEW|APPROVED|PUBLISHED|DEPRECATED';
ALTER TABLE openapi_definition ADD COLUMN reviewer_id BIGINT COMMENT '审核人ID FK to openapi_user.id';
ALTER TABLE openapi_definition ADD COLUMN reviewed_at DATETIME COMMENT '审核时间';
ALTER TABLE openapi_definition ADD COLUMN governance_tags VARCHAR(500) DEFAULT '' COMMENT '治理标签，逗号分隔: compliance,security_reviewed,performance_tested';

CREATE INDEX IF NOT EXISTS idx_definition_lifecycle ON openapi_definition(lifecycle_status);

-- ============================================================
-- 1. Organization — 企业组织
-- ============================================================
CREATE TABLE IF NOT EXISTS enterprise_organization (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(200) NOT NULL COMMENT '组织名称',
    code            VARCHAR(100) NOT NULL COMMENT '组织唯一编码',
    type            VARCHAR(30)  NOT NULL DEFAULT 'ENTERPRISE' COMMENT 'ENTERPRISE|PARTNER|INTERNAL',
    owner_id        BIGINT COMMENT '组织所有者 FK to openapi_user.id',
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE|INACTIVE|SUSPENDED',
    description     TEXT DEFAULT '' COMMENT '组织描述',
    logo_url        VARCHAR(500) DEFAULT '' COMMENT 'Logo URL',
    website         VARCHAR(500) DEFAULT '' COMMENT '官网',
    contact_email   VARCHAR(200) DEFAULT '' COMMENT '联系邮箱',
    contact_phone   VARCHAR(30) DEFAULT '' COMMENT '联系电话',
    tenant_id       VARCHAR(64)  NOT NULL COMMENT '租户ID，组织级别隔离',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user     VARCHAR(100) DEFAULT '',
    update_user     VARCHAR(100) DEFAULT ''
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_org_code ON enterprise_organization(code);
CREATE INDEX IF NOT EXISTS idx_org_type ON enterprise_organization(type);
CREATE INDEX IF NOT EXISTS idx_org_status ON enterprise_organization(status);
CREATE INDEX IF NOT EXISTS idx_org_tenant ON enterprise_organization(tenant_id);

-- ============================================================
-- 2. Team — 组织下团队
-- ============================================================
CREATE TABLE IF NOT EXISTS enterprise_team (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    organization_id BIGINT NOT NULL COMMENT 'FK to enterprise_organization.id',
    parent_id       BIGINT DEFAULT NULL COMMENT '父团队ID，NULL=顶级团队',
    name            VARCHAR(200) NOT NULL COMMENT '团队名称',
    description     VARCHAR(500) DEFAULT '' COMMENT '团队描述',
    leader_id       BIGINT COMMENT '团队负责人 FK to openapi_user.id',
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE|INACTIVE',
    sort_order      INT DEFAULT 0 COMMENT '排序',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user     VARCHAR(100) DEFAULT '',
    update_user     VARCHAR(100) DEFAULT ''
);
CREATE INDEX IF NOT EXISTS idx_team_org ON enterprise_team(organization_id);
CREATE INDEX IF NOT EXISTS idx_team_parent ON enterprise_team(parent_id);
CREATE INDEX IF NOT EXISTS idx_team_leader ON enterprise_team(leader_id);

-- ============================================================
-- 3. Member — 组织成员
-- ============================================================
CREATE TABLE IF NOT EXISTS enterprise_member (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    organization_id BIGINT NOT NULL COMMENT 'FK to enterprise_organization.id',
    team_id         BIGINT COMMENT 'FK to enterprise_team.id，NULL=未分配团队',
    user_id         BIGINT NOT NULL COMMENT 'FK to openapi_user.id',
    role            VARCHAR(30)  NOT NULL DEFAULT 'MEMBER' COMMENT 'OWNER|ADMIN|MEMBER|VIEWER',
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE|INACTIVE|INVITED',
    joined_at       DATETIME COMMENT '加入时间',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user     VARCHAR(100) DEFAULT '',
    update_user     VARCHAR(100) DEFAULT ''
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_member_org_user ON enterprise_member(organization_id, user_id);
CREATE INDEX IF NOT EXISTS idx_member_team ON enterprise_member(team_id);
CREATE INDEX IF NOT EXISTS idx_member_user ON enterprise_member(user_id);
CREATE INDEX IF NOT EXISTS idx_member_role ON enterprise_member(role);

-- ============================================================
-- 4. Partner — 合作伙伴（独立实体）
-- ============================================================
CREATE TABLE IF NOT EXISTS enterprise_partner (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    organization_id BIGINT COMMENT '关联组织 FK to enterprise_organization.id',
    name            VARCHAR(200) NOT NULL COMMENT '合作伙伴名称',
    level           VARCHAR(30)  NOT NULL DEFAULT 'STANDARD' COMMENT 'STANDARD|PREMIUM|STRATEGIC',
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE|INACTIVE|PENDING',
    contact_name    VARCHAR(100) DEFAULT '' COMMENT '联系人',
    contact_email   VARCHAR(200) DEFAULT '' COMMENT '联系邮箱',
    contact_phone   VARCHAR(30) DEFAULT '' COMMENT '联系电话',
    description     TEXT DEFAULT '' COMMENT '备注',
    tenant_id       VARCHAR(64)  DEFAULT '' COMMENT '租户ID',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user     VARCHAR(100) DEFAULT '',
    update_user     VARCHAR(100) DEFAULT ''
);
CREATE INDEX IF NOT EXISTS idx_partner_org ON enterprise_partner(organization_id);
CREATE INDEX IF NOT EXISTS idx_partner_level ON enterprise_partner(level);
CREATE INDEX IF NOT EXISTS idx_partner_status ON enterprise_partner(status);
CREATE INDEX IF NOT EXISTS idx_partner_tenant ON enterprise_partner(tenant_id);

-- ============================================================
-- 5. Partner-API Access — 合作伙伴可访问的API
-- ============================================================
CREATE TABLE IF NOT EXISTS enterprise_partner_api (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    partner_id      BIGINT NOT NULL COMMENT 'FK to enterprise_partner.id',
    api_id          BIGINT NOT NULL COMMENT 'FK to openapi_definition.id',
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE|REVOKED',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_partner_api ON enterprise_partner_api(partner_id, api_id);

-- ============================================================
-- 6. Enterprise Contract — 企业合同
-- ============================================================
CREATE TABLE IF NOT EXISTS enterprise_contract (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    organization_id BIGINT NOT NULL COMMENT 'FK to enterprise_organization.id',
    contract_no     VARCHAR(100) NOT NULL COMMENT '合同编号',
    name            VARCHAR(200) NOT NULL COMMENT '合同名称',
    plan_name       VARCHAR(100) DEFAULT '' COMMENT '套餐名称: Basic|Professional|Enterprise',
    start_date      DATE COMMENT '开始日期',
    end_date        DATE COMMENT '结束日期',
    status          VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT|ACTIVE|EXPIRED|TERMINATED',
    max_requests    BIGINT DEFAULT 0 COMMENT '最大请求数/月，0=不限制',
    max_qps         INT DEFAULT 0 COMMENT '最大QPS，0=不限制',
    supports_phone  TINYINT DEFAULT 0 COMMENT '是否包含电话支持',
    supports_724    TINYINT DEFAULT 0 COMMENT '是否7x24支持',
    description     TEXT DEFAULT '' COMMENT '合同备注',
    tenant_id       VARCHAR(64)  DEFAULT '' COMMENT '租户ID',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user     VARCHAR(100) DEFAULT '',
    update_user     VARCHAR(100) DEFAULT ''
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_contract_no ON enterprise_contract(contract_no);
CREATE INDEX IF NOT EXISTS idx_contract_org ON enterprise_contract(organization_id);
CREATE INDEX IF NOT EXISTS idx_contract_status ON enterprise_contract(status);
CREATE INDEX IF NOT EXISTS idx_contract_date ON enterprise_contract(start_date, end_date);

-- ============================================================
-- 7. SLA Policy — 服务等级协议（Organization 级别）
-- ============================================================
CREATE TABLE IF NOT EXISTS enterprise_sla_policy (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    organization_id     BIGINT NOT NULL COMMENT 'FK to enterprise_organization.id',
    name                VARCHAR(200) NOT NULL COMMENT 'SLA名称',
    availability        DECIMAL(5,4) NOT NULL DEFAULT 0.9900 COMMENT '可用性目标: 0.99=99%, 0.9999=99.99%',
    response_time_ms    INT DEFAULT 1000 COMMENT '响应时间目标（毫秒）',
    latency_p99_ms      INT DEFAULT 500 COMMENT 'P99延迟目标（毫秒）',
    support_level       VARCHAR(30) DEFAULT 'STANDARD' COMMENT 'STANDARD|PRIORITY|PREMIUM',
    incident_response_min INT DEFAULT 60 COMMENT '故障响应时间（分钟）',
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE|INACTIVE',
    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user         VARCHAR(100) DEFAULT '',
    update_user         VARCHAR(100) DEFAULT ''
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_sla_org ON enterprise_sla_policy(organization_id);
CREATE INDEX IF NOT EXISTS idx_sla_status ON enterprise_sla_policy(status);

-- ============================================================
-- 8. Enterprise Billing Account — 基础计费账户
-- ============================================================
CREATE TABLE IF NOT EXISTS enterprise_billing (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    organization_id BIGINT NOT NULL COMMENT 'FK to enterprise_organization.id',
    account_name    VARCHAR(200) NOT NULL COMMENT '账户名称',
    balance         DECIMAL(15,2) NOT NULL DEFAULT 0 COMMENT '余额',
    currency        VARCHAR(10) DEFAULT 'CNY' COMMENT '币种',
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE|FROZEN|CLOSED',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user     VARCHAR(100) DEFAULT '',
    update_user     VARCHAR(100) DEFAULT ''
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_billing_org ON enterprise_billing(organization_id);

-- ============================================================
-- 9. Enterprise Billing Record — 计费记录
-- ============================================================
CREATE TABLE IF NOT EXISTS enterprise_billing_record (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    billing_id      BIGINT NOT NULL COMMENT 'FK to enterprise_billing.id',
    type            VARCHAR(20) NOT NULL COMMENT 'CHARGE|REFUND|ADJUSTMENT',
    amount          DECIMAL(15,2) NOT NULL DEFAULT 0 COMMENT '金额',
    description     VARCHAR(500) DEFAULT '' COMMENT '描述',
    reference_id    VARCHAR(100) DEFAULT '' COMMENT '关联业务ID',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_billing_rec_billing ON enterprise_billing_record(billing_id);
CREATE INDEX IF NOT EXISTS idx_billing_rec_time ON enterprise_billing_record(create_time);

-- ============================================================
-- 10. Compliance Policy — 合规策略
-- ============================================================
CREATE TABLE IF NOT EXISTS enterprise_compliance (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    organization_id BIGINT NOT NULL COMMENT 'FK to enterprise_organization.id',
    name            VARCHAR(200) NOT NULL COMMENT '策略名称',
    policy_type     VARCHAR(50) NOT NULL COMMENT 'DATA_RETENTION|DATA_MASKING|CROSS_REGION|AUDIT_LEVEL|IP_WHITELIST',
    config_json     TEXT COMMENT '策略配置JSON',
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE|INACTIVE',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user     VARCHAR(100) DEFAULT '',
    update_user     VARCHAR(100) DEFAULT ''
);
CREATE INDEX IF NOT EXISTS idx_compliance_org ON enterprise_compliance(organization_id);
CREATE INDEX IF NOT EXISTS idx_compliance_type ON enterprise_compliance(policy_type);

-- ============================================================
-- 11. Enterprise Identity Provider — 企业身份提供商
-- ============================================================
CREATE TABLE IF NOT EXISTS enterprise_identity (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    organization_id BIGINT NOT NULL COMMENT 'FK to enterprise_organization.id',
    provider_type   VARCHAR(20) NOT NULL COMMENT 'LDAP|SAML|OAUTH2|OIDC|SSO',
    name            VARCHAR(200) NOT NULL COMMENT 'Provider名称',
    config_json     TEXT COMMENT 'Provider配置JSON（不存密码）',
    is_default      TINYINT DEFAULT 0 COMMENT '是否默认Provider',
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE|INACTIVE',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user     VARCHAR(100) DEFAULT '',
    update_user     VARCHAR(100) DEFAULT ''
);
CREATE INDEX IF NOT EXISTS idx_identity_org ON enterprise_identity(organization_id);
CREATE INDEX IF NOT EXISTS idx_identity_type ON enterprise_identity(provider_type);

-- ============================================================
-- 12. Enterprise Settings — 企业级设置
-- ============================================================
CREATE TABLE IF NOT EXISTS enterprise_setting (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    organization_id BIGINT NOT NULL UNIQUE COMMENT 'FK to enterprise_organization.id',
    domain          VARCHAR(200) DEFAULT '' COMMENT '企业自定义域名',
    enable_sso      TINYINT DEFAULT 0 COMMENT '是否强制SSO登录',
    enable_audit    TINYINT DEFAULT 1 COMMENT '是否启用审计',
    data_region     VARCHAR(20) DEFAULT '' COMMENT '数据存储区域',
    retention_days  INT DEFAULT 90 COMMENT '日志保留天数',
    timezone        VARCHAR(50) DEFAULT 'Asia/Shanghai' COMMENT '时区',
    language        VARCHAR(10) DEFAULT 'zh-CN' COMMENT '默认语言',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- Seed Data: 默认组织
-- ============================================================
INSERT INTO enterprise_organization (name, code, type, status, description, tenant_id, create_time, update_time, create_user)
VALUES ('Core Platform Internal', 'CORE_INTERNAL', 'INTERNAL', 'ACTIVE', 'Core Platform 内部组织', 'default', datetime('now'), datetime('now'), 'system');
