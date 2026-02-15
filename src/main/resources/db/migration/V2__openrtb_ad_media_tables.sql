-- =============================================================================
-- OpenRTB DSP: 광고 요청·응답 수집, 광고(소재)·매체 정보 테이블
-- =============================================================================

-- 1) 광고 요청 수집 (수신한 OpenRTB Bid Request)
CREATE TABLE bid_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id VARCHAR(64) NOT NULL,
    raw_json CLOB,
    imp_ids VARCHAR(512),
    site_id VARCHAR(64),
    app_id VARCHAR(64),
    publisher_id VARCHAR(64),
    received_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_bid_requests_request_id ON bid_requests(request_id);
CREATE INDEX idx_bid_requests_received ON bid_requests(received_at);
CREATE INDEX idx_bid_requests_site ON bid_requests(site_id);
CREATE INDEX idx_bid_requests_app ON bid_requests(app_id);

-- 2) 광고 응답 수집 (DSP가 반환한 Bid Response 요약)
CREATE TABLE bid_responses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id VARCHAR(64) NOT NULL,
    bid_response_id VARCHAR(64),
    seat VARCHAR(64),
    bid_impid VARCHAR(64),
    bid_price DECIMAL(19, 4),
    bid_crid VARCHAR(64),
    bid_adid VARCHAR(64),
    raw_json CLOB,
    responded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_bid_responses_request_id ON bid_responses(request_id);
CREATE INDEX idx_bid_responses_responded ON bid_responses(responded_at);
CREATE INDEX idx_bid_responses_crid ON bid_responses(bid_crid);

-- 3) 광고 소재(Creative) 정보
CREATE TABLE creatives (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    creative_id VARCHAR(64) NOT NULL,
    campaign_id VARCHAR(64) NOT NULL,
    name VARCHAR(256),
    format VARCHAR(32) NOT NULL,
    width INT,
    height INT,
    mime_type VARCHAR(128),
    adm_snippet CLOB,
    landing_url VARCHAR(1024),
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_creatives_creative_id ON creatives(creative_id);
CREATE INDEX idx_creatives_campaign ON creatives(campaign_id);
CREATE INDEX idx_creatives_status ON creatives(status);

-- 4) 매체 - 퍼블리셔 (OpenRTB Publisher)
CREATE TABLE publishers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    publisher_id VARCHAR(64) NOT NULL,
    name VARCHAR(256),
    domain VARCHAR(256),
    ext_json CLOB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_publishers_publisher_id ON publishers(publisher_id);

-- 5) 매체 - 사이트 (OpenRTB Site)
CREATE TABLE sites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    site_id VARCHAR(64) NOT NULL,
    publisher_id BIGINT,
    name VARCHAR(256),
    domain VARCHAR(256),
    page VARCHAR(1024),
    ref VARCHAR(1024),
    ext_json CLOB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sites_publisher FOREIGN KEY (publisher_id) REFERENCES publishers(id)
);

CREATE UNIQUE INDEX idx_sites_site_id ON sites(site_id);
CREATE INDEX idx_sites_publisher ON sites(publisher_id);

-- 6) 매체 - 앱 (OpenRTB App)
CREATE TABLE apps (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    app_id VARCHAR(64) NOT NULL,
    publisher_id BIGINT,
    name VARCHAR(256),
    bundle VARCHAR(256),
    domain VARCHAR(256),
    storeurl VARCHAR(1024),
    ver VARCHAR(64),
    ext_json CLOB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_apps_publisher FOREIGN KEY (publisher_id) REFERENCES publishers(id)
);

CREATE UNIQUE INDEX idx_apps_app_id ON apps(app_id);
CREATE INDEX idx_apps_publisher ON apps(publisher_id);

-- 7) events 테이블 확장: 소재·매체 연계용 컬럼 (선택)
ALTER TABLE events ADD COLUMN IF NOT EXISTS crid VARCHAR(64);
ALTER TABLE events ADD COLUMN IF NOT EXISTS impid VARCHAR(64);
ALTER TABLE events ADD COLUMN IF NOT EXISTS site_id VARCHAR(64);
ALTER TABLE events ADD COLUMN IF NOT EXISTS app_id VARCHAR(64);

CREATE INDEX idx_events_crid ON events(crid);
CREATE INDEX idx_events_impid ON events(impid);
