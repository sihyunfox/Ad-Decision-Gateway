-- Decision history: stores each decision result for auditing and debugging
CREATE TABLE decision_history (
    decision_id VARCHAR(64) PRIMARY KEY,
    trace_id VARCHAR(64) NOT NULL,
    request_id VARCHAR(64),
    client_id VARCHAR(64) NOT NULL,
    placement_id VARCHAR(64) NOT NULL,
    winner_campaign_id VARCHAR(64),
    winner_creative_id VARCHAR(64),
    winner_bid DECIMAL(19, 4),
    winner_score DECIMAL(19, 4),
    tracking_url VARCHAR(1024),
    fallback_used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_decision_history_client_created ON decision_history(client_id, created_at);
CREATE INDEX idx_decision_history_trace ON decision_history(trace_id);

-- Events: impression/click with idempotency key
CREATE TABLE events (
    event_id VARCHAR(128) PRIMARY KEY,
    decision_id VARCHAR(64),
    client_id VARCHAR(64) NOT NULL,
    event_type VARCHAR(32) NOT NULL,
    payload_json CLOB,
    event_timestamp TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_events_client_created ON events(client_id, created_at);

-- Policies: filter rules, AB flags, timeout config per client
CREATE TABLE policies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id VARCHAR(64) NOT NULL,
    filter_rules CLOB,
    ab_flags CLOB,
    timeout_config CLOB,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_policies_client_active ON policies(client_id, active);

-- Audit log: policy changes and admin actions
CREATE TABLE audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(64) NOT NULL,
    entity_id VARCHAR(128) NOT NULL,
    action VARCHAR(32) NOT NULL,
    old_value CLOB,
    new_value CLOB,
    actor VARCHAR(128),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_log_entity ON audit_log(entity_type, entity_id);
CREATE INDEX idx_audit_log_created ON audit_log(created_at);
