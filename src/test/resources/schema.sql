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
