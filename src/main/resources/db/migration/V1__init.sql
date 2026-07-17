-- ============================================================
-- core-openapi: API Foundation Runtime — Phase 0
-- Tables: openapi_service, openapi_definition, openapi_parameter,
--         openapi_response, openapi_version, openapi_tag,
--         openapi_tag_mapping
-- ============================================================

-- 1. API Service Registry
CREATE TABLE IF NOT EXISTS openapi_service (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    service_name  VARCHAR(100)  NOT NULL COMMENT '服务名称',
    service_code  VARCHAR(50)   NOT NULL COMMENT '唯一编码',
    description   VARCHAR(500)  DEFAULT '' COMMENT '描述',
    owner         VARCHAR(100)  DEFAULT '' COMMENT '负责人',
    status        VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE|INACTIVE',
    version       VARCHAR(20)   DEFAULT '1.0' COMMENT '服务版本',
    create_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    create_user   VARCHAR(100)  DEFAULT '' COMMENT '创建人',
    update_user   VARCHAR(100)  DEFAULT '' COMMENT '更新人'
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_service_code ON openapi_service(service_code);

-- 2. API Definition
CREATE TABLE IF NOT EXISTS openapi_definition (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    service_id  BIGINT        NOT NULL COMMENT '所属服务ID',
    name        VARCHAR(100)  NOT NULL COMMENT '接口名称',
    path        VARCHAR(200)  NOT NULL COMMENT '请求路径',
    http_method VARCHAR(10)   NOT NULL COMMENT 'HTTP方法: GET|POST|PUT|DELETE|PATCH',
    description VARCHAR(500)  DEFAULT '' COMMENT '描述',
    category    VARCHAR(50)   DEFAULT '' COMMENT '分类',
    status      VARCHAR(20)   NOT NULL DEFAULT 'DRAFT' COMMENT '生命周期: DRAFT|REVIEW|PUBLISHED|DEPRECATED|OFFLINE',
    create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    create_user VARCHAR(100)  DEFAULT '' COMMENT '创建人',
    update_user VARCHAR(100)  DEFAULT '' COMMENT '更新人'
);
CREATE INDEX IF NOT EXISTS idx_definition_service ON openapi_definition(service_id);
CREATE INDEX IF NOT EXISTS idx_definition_status ON openapi_definition(status);

-- 3. API Parameter
CREATE TABLE IF NOT EXISTS openapi_parameter (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    api_id      BIGINT        NOT NULL COMMENT '所属接口ID',
    name        VARCHAR(100)  NOT NULL COMMENT '参数名',
    location    VARCHAR(10)   NOT NULL COMMENT '位置: PATH|QUERY|HEADER|BODY',
    type        VARCHAR(20)   NOT NULL COMMENT '参数类型: String|Long|Integer|Boolean等',
    required    TINYINT       DEFAULT 0 COMMENT '是否必填: 0-否 1-是',
    description VARCHAR(500)  DEFAULT '' COMMENT '描述',
    example     VARCHAR(500)  DEFAULT '' COMMENT '示例值',
    sort_order  INT           DEFAULT 0 COMMENT '排序',
    create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);
CREATE INDEX IF NOT EXISTS idx_parameter_api ON openapi_parameter(api_id);

-- 4. API Response
CREATE TABLE IF NOT EXISTS openapi_response (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    api_id       BIGINT        NOT NULL COMMENT '所属接口ID',
    status_code  VARCHAR(5)    NOT NULL COMMENT 'HTTP状态码: 200|400|401|500等',
    content_type VARCHAR(50)   DEFAULT 'application/json' COMMENT '内容类型',
    schema       TEXT          COMMENT '响应结构(JSON)',
    example      TEXT          COMMENT '示例值',
    create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);
CREATE INDEX IF NOT EXISTS idx_response_api ON openapi_response(api_id);

-- 5. API Version
CREATE TABLE IF NOT EXISTS openapi_version (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    api_id          BIGINT        NOT NULL COMMENT '所属接口ID',
    version         VARCHAR(20)   NOT NULL COMMENT '版本号: 1.0|2.0',
    status          VARCHAR(20)   NOT NULL DEFAULT 'DRAFT' COMMENT '版本状态: DRAFT|ACTIVE|DEPRECATED|DISABLED',
    changelog       TEXT          COMMENT '变更说明',
    release_time    DATETIME      COMMENT '发布时间',
    deprecated_time DATETIME      COMMENT '废弃时间',
    create_time     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);
CREATE INDEX IF NOT EXISTS idx_version_api ON openapi_version(api_id);
CREATE INDEX IF NOT EXISTS idx_version_status ON openapi_version(status);

-- 6. API Tag
CREATE TABLE IF NOT EXISTS openapi_tag (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(50)   NOT NULL COMMENT '标签名',
    color       VARCHAR(20)   DEFAULT '#666' COMMENT '颜色',
    create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_tag_name ON openapi_tag(name);

-- 7. API Tag Mapping (many-to-many)
CREATE TABLE IF NOT EXISTS openapi_tag_mapping (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    tag_id      BIGINT        NOT NULL COMMENT '标签ID',
    api_id      BIGINT        NOT NULL COMMENT '接口ID',
    create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_tag_api ON openapi_tag_mapping(tag_id, api_id);
CREATE INDEX IF NOT EXISTS idx_mapping_api ON openapi_tag_mapping(api_id);
