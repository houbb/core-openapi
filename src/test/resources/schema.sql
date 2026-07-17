CREATE TABLE openapi_service (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL,
    service_code VARCHAR(50) NOT NULL,
    description VARCHAR(500) DEFAULT '',
    owner VARCHAR(100) DEFAULT '',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    version VARCHAR(20) DEFAULT '1.0',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user VARCHAR(100) DEFAULT '',
    update_user VARCHAR(100) DEFAULT ''
);
CREATE UNIQUE INDEX uk_service_code ON openapi_service(service_code);

CREATE TABLE openapi_definition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    path VARCHAR(200) NOT NULL,
    http_method VARCHAR(10) NOT NULL,
    description VARCHAR(500) DEFAULT '',
    category VARCHAR(50) DEFAULT '',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user VARCHAR(100) DEFAULT '',
    update_user VARCHAR(100) DEFAULT ''
);

CREATE TABLE openapi_parameter (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    api_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(10) NOT NULL,
    type VARCHAR(20) NOT NULL,
    required TINYINT DEFAULT 0,
    description VARCHAR(500) DEFAULT '',
    example VARCHAR(500) DEFAULT '',
    sort_order INT DEFAULT 0,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE openapi_response (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    api_id BIGINT NOT NULL,
    status_code VARCHAR(5) NOT NULL,
    content_type VARCHAR(50) DEFAULT 'application/json',
    schema TEXT,
    example TEXT,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE openapi_version (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    api_id BIGINT NOT NULL,
    version VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    changelog TEXT,
    release_time TIMESTAMP,
    deprecated_time TIMESTAMP,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE openapi_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    color VARCHAR(20) DEFAULT '#666',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE openapi_tag_mapping (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tag_id BIGINT NOT NULL,
    api_id BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX uk_tag_api ON openapi_tag_mapping(tag_id, api_id);

-- Phase 1: Gateway tables
CREATE TABLE openapi_route (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    api_id BIGINT NOT NULL,
    service_name VARCHAR(100) NOT NULL,
    target_url VARCHAR(500) NOT NULL,
    timeout INT DEFAULT 30000,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE openapi_access_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id VARCHAR(64) NOT NULL,
    api_id BIGINT,
    client_id VARCHAR(100) DEFAULT '',
    request_method VARCHAR(10) NOT NULL,
    request_path VARCHAR(500) NOT NULL,
    target_url VARCHAR(500) DEFAULT '',
    request_time TIMESTAMP NOT NULL,
    response_time TIMESTAMP,
    status_code INT NOT NULL DEFAULT 0,
    cost_time BIGINT DEFAULT 0,
    error_message TEXT,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE openapi_gateway_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL,
    config_value VARCHAR(500) NOT NULL,
    description VARCHAR(500) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Phase 2: Definition tables
CREATE TABLE openapi_request_schema (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    api_id BIGINT NOT NULL,
    content_type VARCHAR(50) DEFAULT 'application/json',
    schema_json TEXT,
    example_json TEXT,
    description VARCHAR(500) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE openapi_example (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    api_id BIGINT NOT NULL,
    type VARCHAR(10) NOT NULL,
    name VARCHAR(100) DEFAULT '',
    content TEXT,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE openapi_response ADD COLUMN IF NOT EXISTS description VARCHAR(500) DEFAULT '';

-- Phase 3: API Key tables
CREATE TABLE openapi_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(200) DEFAULT '',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX uk_user_username ON openapi_user(username);

CREATE TABLE openapi_application (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    app_name VARCHAR(100) NOT NULL,
    app_code VARCHAR(100) NOT NULL,
    owner_id BIGINT,
    description TEXT DEFAULT '',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX uk_app_code ON openapi_application(app_code);

CREATE TABLE openapi_api_key (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    key_prefix VARCHAR(20) NOT NULL,
    key_hash VARCHAR(64) NOT NULL,
    name VARCHAR(100) DEFAULT '',
    environment VARCHAR(10) NOT NULL DEFAULT 'LIVE',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    expire_time TIMESTAMP,
    last_used_time TIMESTAMP,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE api_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500) DEFAULT ''
);
CREATE UNIQUE INDEX uk_perm_name ON api_permission(name);

CREATE TABLE application_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL
);

CREATE TABLE api_key_usage_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    api_key_id BIGINT NOT NULL,
    api_id BIGINT,
    request_id VARCHAR(64) DEFAULT '',
    ip VARCHAR(50) DEFAULT '',
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'SUCCESS'
);
