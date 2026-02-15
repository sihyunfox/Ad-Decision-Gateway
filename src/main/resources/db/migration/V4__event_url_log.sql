-- Event URL 로그 (노출/클릭/nurl/burl/lurl 5종)
CREATE TABLE event_url_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(32) NOT NULL,
    url VARCHAR(2048) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_event_url_log_event_type ON event_url_log(event_type);
CREATE INDEX idx_event_url_log_created_at ON event_url_log(created_at);
