-- ============================================================
-- Phase 7: Developer Portal Runtime
-- V8__portal.sql
-- ============================================================

-- 1. ALTER existing openapi_user table: add developer portal fields
-- Reuse openapi_user as developer account identity
ALTER TABLE openapi_user ADD COLUMN developer_type VARCHAR(20) DEFAULT '' COMMENT 'INDIVIDUAL|COMPANY|PARTNER (empty = legacy/non-portal)';
ALTER TABLE openapi_user ADD COLUMN password_hash VARCHAR(128) DEFAULT '' COMMENT 'BCrypt hashed password for portal login';
ALTER TABLE openapi_user ADD COLUMN avatar_url VARCHAR(500) DEFAULT '' COMMENT 'Avatar URL';
ALTER TABLE openapi_user ADD COLUMN company_name VARCHAR(200) DEFAULT '' COMMENT 'Company name (for COMPANY type)';
ALTER TABLE openapi_user ADD COLUMN phone VARCHAR(30) DEFAULT '' COMMENT 'Phone number';

CREATE INDEX IF NOT EXISTS idx_user_devtype ON openapi_user(developer_type);

-- 2. api_subscription — Developer subscribes to APIs with a plan
CREATE TABLE IF NOT EXISTS api_subscription (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id         BIGINT NOT NULL COMMENT 'FK to openapi_user.id',
    api_id          BIGINT NOT NULL COMMENT 'FK to openapi_definition.id',
    plan            VARCHAR(20) NOT NULL DEFAULT 'FREE' COMMENT 'FREE|STARTER|ENTERPRISE',
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE|CANCELLED|EXPIRED',
    max_qps         INTEGER DEFAULT 100 COMMENT 'Max QPS allowed',
    max_daily       INTEGER DEFAULT 1000 COMMENT 'Max daily calls',
    subscribed_at   DATETIME NOT NULL,
    expired_at      DATETIME,
    create_time     DATETIME NOT NULL,
    update_time     DATETIME NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_sub_user_api ON api_subscription(user_id, api_id);
CREATE INDEX IF NOT EXISTS idx_sub_api ON api_subscription(api_id);
CREATE INDEX IF NOT EXISTS idx_sub_status ON api_subscription(status);

-- 3. developer_notification — Notifications for developers
CREATE TABLE IF NOT EXISTS developer_notification (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id         BIGINT NOT NULL COMMENT 'FK to openapi_user.id',
    title           VARCHAR(200) NOT NULL,
    content         TEXT DEFAULT '',
    type            VARCHAR(30) NOT NULL DEFAULT 'INFO' COMMENT 'INFO|WARNING|SUCCESS|ERROR',
    is_read         TINYINT DEFAULT 0 COMMENT '0-unread 1-read',
    link            VARCHAR(500) DEFAULT '' COMMENT 'Optional action link',
    create_time     DATETIME NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_notif_user ON developer_notification(user_id);
CREATE INDEX IF NOT EXISTS idx_notif_read ON developer_notification(user_id, is_read);

-- 4. developer_setting — Per-developer settings/preferences
CREATE TABLE IF NOT EXISTS developer_setting (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id         BIGINT NOT NULL UNIQUE COMMENT 'FK to openapi_user.id',
    language        VARCHAR(10) DEFAULT 'zh-CN',
    theme           VARCHAR(10) DEFAULT 'light' COMMENT 'light|dark',
    notify_email    TINYINT DEFAULT 1,
    notify_usage    TINYINT DEFAULT 1,
    create_time     DATETIME NOT NULL,
    update_time     DATETIME NOT NULL
);

-- 5. portal_document — Developer-facing documentation articles
CREATE TABLE IF NOT EXISTS portal_document (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    title           VARCHAR(200) NOT NULL,
    slug            VARCHAR(200) NOT NULL COMMENT 'URL-friendly unique key',
    category        VARCHAR(50) NOT NULL COMMENT 'GETTING_STARTED|API_REFERENCE|SDK_GUIDE|FAQ|CHANGELOG',
    content         TEXT NOT NULL COMMENT 'Markdown content',
    sort_order      INTEGER DEFAULT 0,
    status          VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED' COMMENT 'DRAFT|PUBLISHED|ARCHIVED',
    author          VARCHAR(100) DEFAULT '',
    create_time     DATETIME NOT NULL,
    update_time     DATETIME NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_doc_slug ON portal_document(slug);
CREATE INDEX IF NOT EXISTS idx_doc_category ON portal_document(category);
CREATE INDEX IF NOT EXISTS idx_doc_status ON portal_document(status);

-- 6. portal_feedback — User feedback/suggestions
CREATE TABLE IF NOT EXISTS portal_feedback (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id         BIGINT NOT NULL COMMENT 'FK to openapi_user.id',
    type            VARCHAR(30) NOT NULL DEFAULT 'SUGGESTION' COMMENT 'SUGGESTION|BUG|QUESTION|OTHER',
    title           VARCHAR(200) NOT NULL,
    content         TEXT DEFAULT '',
    status          VARCHAR(20) NOT NULL DEFAULT 'OPEN' COMMENT 'OPEN|IN_PROGRESS|RESOLVED|CLOSED',
    admin_reply     TEXT DEFAULT '',
    create_time     DATETIME NOT NULL,
    update_time     DATETIME NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_fb_user ON portal_feedback(user_id);
CREATE INDEX IF NOT EXISTS idx_fb_status ON portal_feedback(status);

-- Seed data: default getting-started doc
INSERT INTO portal_document (title, slug, category, content, sort_order, status, author, create_time, update_time)
VALUES (
    'Getting Started',
    'getting-started',
    'GETTING_STARTED',
    '# Getting Started with Core OpenAPI

Welcome to the Core OpenAPI Developer Portal! Follow these steps to make your first API call.

## 1. Create an Account

Register for a developer account at [the registration page](/register).

## 2. Create an Application

Applications represent your project or service. Go to **My Applications** and click **Create Application**.

## 3. Subscribe to an API

Browse the [API Catalog](/catalog) and subscribe to the APIs you need.

## 4. Generate an API Key

In your application details, generate an API Key. You''ll use this key to authenticate your API calls.

## 5. Make Your First Call

Use the [API Playground](/app/playground) to test APIs without writing any code.

Or copy the SDK snippet and integrate directly into your project.

## Need Help?

Check the [API Reference](/docs/api-reference) or [SDK Guide](/docs/sdk-guide).',
    1,
    'PUBLISHED',
    'system',
    datetime('now'),
    datetime('now')
);

INSERT INTO portal_document (title, slug, category, content, sort_order, status, author, create_time, update_time)
VALUES (
    'API Reference',
    'api-reference',
    'API_REFERENCE',
    '# API Reference

This section documents all available APIs in the Core OpenAPI platform.

Browse the [API Catalog](/catalog) to explore available endpoints, request/response formats, and examples.

Each API detail page includes:
- Endpoint URL and HTTP method
- Request parameters (path, query, header, body)
- Response schema
- Code examples
- Try-it playground',
    2,
    'PUBLISHED',
    'system',
    datetime('now'),
    datetime('now')
);

INSERT INTO portal_document (title, slug, category, content, sort_order, status, author, create_time, update_time)
VALUES (
    'SDK Guide',
    'sdk-guide',
    'SDK_GUIDE',
    '# SDK Guide

We provide official SDKs for popular languages to help you integrate faster.

## Available SDKs

- **Java** — Maven dependency, Spring Boot integration
- **Python** — pip package, async support

## Installation

### Java (Maven)

```xml
<dependency>
    <groupId>io.coreplatform</groupId>
    <artifactId>core-openapi-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Python

```bash
pip install core-openapi-sdk
```

## Quick Start

Check the [SDK Center](/sdk) to download the latest SDK with your selected APIs pre-configured.',
    3,
    'PUBLISHED',
    'system',
    datetime('now'),
    datetime('now')
);