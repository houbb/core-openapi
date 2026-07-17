-- ============================================================
-- core-openapi: Rate Limit Runtime — Phase 6
-- Tables: rate_limit_policy, rate_limit_usage
-- ============================================================

-- ============================================================
-- 1. Rate Limit Policy
-- Stores rate limit policies for API-level and Application-level
-- ============================================================
CREATE TABLE rate_limit_policy
(
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    name          VARCHAR(100)  NOT NULL,
    scope         VARCHAR(50)   NOT NULL,          -- API / APPLICATION
    scope_id      INTEGER,                          -- api_id or application_id
    algorithm     VARCHAR(50)   NOT NULL DEFAULT 'TOKEN_BUCKET',
    limit_value   INTEGER       NOT NULL DEFAULT 100,
    refill_rate   INTEGER       NOT NULL DEFAULT 10,
    refill_period INTEGER       NOT NULL DEFAULT 1, -- seconds
    status        VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE',
    description   VARCHAR(500),
    create_time   DATETIME      NOT NULL,
    update_time   DATETIME      NOT NULL
);

CREATE INDEX idx_rlp_scope ON rate_limit_policy(scope, scope_id);
CREATE INDEX idx_rlp_status ON rate_limit_policy(status);

-- ============================================================
-- 2. Rate Limit Usage Record
-- Aggregated usage stats per policy per time window
-- ============================================================
CREATE TABLE rate_limit_usage
(
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    policy_id       INTEGER,
    api_id          INTEGER,
    application_id  INTEGER,
    identity_id     VARCHAR(100),
    request_count   INTEGER       NOT NULL DEFAULT 1,
    blocked_count   INTEGER       NOT NULL DEFAULT 0,
    recorded_at     DATETIME      NOT NULL,
    create_time     DATETIME      NOT NULL
);

CREATE INDEX idx_rlu_policy ON rate_limit_usage(policy_id);
CREATE INDEX idx_rlu_identity ON rate_limit_usage(identity_id);
CREATE INDEX idx_rlu_recorded ON rate_limit_usage(recorded_at);
