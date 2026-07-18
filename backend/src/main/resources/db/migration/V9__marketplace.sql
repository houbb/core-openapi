-- ============================================================
-- Phase 8: API Marketplace Runtime
-- V9__marketplace.sql
-- ============================================================

-- 1. api_provider — API 提供者/Provider
CREATE TABLE IF NOT EXISTS api_provider (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    name            VARCHAR(100) NOT NULL COMMENT 'Provider 名称',
    description     TEXT DEFAULT '' COMMENT 'Provider 描述',
    type            VARCHAR(20) NOT NULL DEFAULT 'COMMUNITY' COMMENT 'OFFICIAL|PARTNER|COMMUNITY|ENTERPRISE',
    owner_id        BIGINT COMMENT 'FK to openapi_user.id',
    verified        TINYINT DEFAULT 0 COMMENT '0-unverified 1-verified',
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE|INACTIVE',
    contact_email   VARCHAR(100) DEFAULT '',
    website         VARCHAR(500) DEFAULT '',
    logo_url        VARCHAR(500) DEFAULT '',
    create_time     DATETIME NOT NULL,
    update_time     DATETIME NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_provider_type ON api_provider(type);
CREATE INDEX IF NOT EXISTS idx_provider_status ON api_provider(status);

-- 2. api_product — API 商品，将 openapi_definition 包装为商品
CREATE TABLE IF NOT EXISTS api_product (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    name            VARCHAR(100) NOT NULL COMMENT '商品名称',
    description     TEXT DEFAULT '' COMMENT '商品描述',
    provider_id     BIGINT NOT NULL COMMENT 'FK to api_provider.id',
    api_id          BIGINT NOT NULL COMMENT 'FK to openapi_definition.id',
    category        VARCHAR(50) DEFAULT 'OTHER' COMMENT 'AI|DATA|STORAGE|WORKFLOW|NOTIFICATION|PLUGIN|OTHER',
    icon_url        VARCHAR(500) DEFAULT '',
    status          VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT|PENDING_REVIEW|PUBLISHED|DEPRECATED',
    create_time     DATETIME NOT NULL,
    update_time     DATETIME NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_product_provider ON api_product(provider_id);
CREATE INDEX IF NOT EXISTS idx_product_api ON api_product(api_id);
CREATE INDEX IF NOT EXISTS idx_product_category ON api_product(category);
CREATE INDEX IF NOT EXISTS idx_product_status ON api_product(status);

-- 3. api_plan — 定价计划
CREATE TABLE IF NOT EXISTS api_plan (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    product_id      BIGINT NOT NULL COMMENT 'FK to api_product.id',
    name            VARCHAR(100) NOT NULL COMMENT '套餐名称: Free|Basic|Pro|Enterprise',
    description     TEXT DEFAULT '',
    price           DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '价格，0=免费',
    billing_type    VARCHAR(20) NOT NULL DEFAULT 'FREE' COMMENT 'FREE|PER_REQUEST|TOKEN_BASED|TIERED',
    limit_config    TEXT DEFAULT '' COMMENT 'JSON: quotas配置, e.g. {"maxQps":100,"maxDaily":1000,"maxTokens":100000}',
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE|INACTIVE',
    sort_order      INTEGER DEFAULT 0,
    create_time     DATETIME NOT NULL,
    update_time     DATETIME NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_plan_product ON api_plan(product_id);

-- 4. api_listing — 商品上架展示信息
CREATE TABLE IF NOT EXISTS api_listing (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    product_id      BIGINT NOT NULL UNIQUE COMMENT 'FK to api_product.id',
    featured        TINYINT DEFAULT 0 COMMENT '0-normal 1-featured (首页推荐)',
    sort_order      INTEGER DEFAULT 0 COMMENT '排序权重，越大越靠前',
    tags            VARCHAR(500) DEFAULT '' COMMENT '逗号分隔标签',
    highlight_text  VARCHAR(200) DEFAULT '' COMMENT '亮点文案',
    create_time     DATETIME NOT NULL,
    update_time     DATETIME NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_listing_featured ON api_listing(featured, sort_order);

-- 5. api_review — 用户评价
CREATE TABLE IF NOT EXISTS api_review (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    product_id      BIGINT NOT NULL COMMENT 'FK to api_product.id',
    user_id         BIGINT NOT NULL COMMENT 'FK to openapi_user.id',
    rating          INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5) COMMENT '评分 1-5',
    comment         TEXT DEFAULT '' COMMENT '评价内容',
    create_time     DATETIME NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_review_product ON api_review(product_id);
CREATE INDEX IF NOT EXISTS idx_review_user ON api_review(user_id);

-- 6. api_usage_record — Provider 使用统计（按日汇总）
CREATE TABLE IF NOT EXISTS api_usage_record (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    product_id      BIGINT NOT NULL COMMENT 'FK to api_product.id',
    api_id          BIGINT NOT NULL COMMENT 'FK to openapi_definition.id',
    user_id         BIGINT NOT NULL COMMENT 'FK to openapi_user.id',
    request_count   BIGINT DEFAULT 0 COMMENT '请求次数',
    token_count     BIGINT DEFAULT 0 COMMENT 'Token 消耗量',
    error_count     BIGINT DEFAULT 0 COMMENT '错误次数',
    recorded_date   DATE NOT NULL COMMENT '统计日期',
    create_time     DATETIME NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_usage_record ON api_usage_record(product_id, api_id, user_id, recorded_date);
CREATE INDEX IF NOT EXISTS idx_usage_date ON api_usage_record(recorded_date);

-- 7. ALTER api_subscription: add plan_id
ALTER TABLE api_subscription ADD COLUMN plan_id BIGINT DEFAULT NULL COMMENT 'FK to api_plan.id';

-- Seed data: default official provider
INSERT INTO api_provider (name, description, type, verified, status, contact_email, create_time, update_time)
VALUES ('Core Platform Official', 'Core Platform 官方 API 提供者', 'OFFICIAL', 1, 'ACTIVE', 'api@coreplatform.io', datetime('now'), datetime('now'));
