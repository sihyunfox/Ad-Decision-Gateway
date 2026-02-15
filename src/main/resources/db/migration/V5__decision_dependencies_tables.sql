-- =============================================================================
-- Decision 의존 데이터: Profile, Campaign(placement), Cap. Policy는 policies 사용.
-- =============================================================================

-- 1) 유저 프로필 (세그먼트, 관심사, 리스크 스코어)
CREATE TABLE profiles (
    user_id VARCHAR(64) PRIMARY KEY,
    segment CLOB,
    interests CLOB,
    risk_score DOUBLE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2) placement별 후보 캠페인 (CampaignItem 1건 = 1행)
CREATE TABLE campaign_placements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    placement_id VARCHAR(64) NOT NULL,
    campaign_id VARCHAR(64) NOT NULL,
    creative_id VARCHAR(64) NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
    budget DECIMAL(19, 4),
    bid DECIMAL(19, 4) NOT NULL,
    target_conditions CLOB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_campaign_placements_placement_id ON campaign_placements(placement_id);

-- 3) Cap 검사 (남은 허용량). campaign_id '*' = 전체 대표
CREATE TABLE cap_allowance (
    client_id VARCHAR(64) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    campaign_id VARCHAR(64) NOT NULL,
    remaining INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (client_id, user_id, campaign_id)
);

CREATE INDEX idx_cap_allowance_lookup ON cap_allowance(client_id, user_id);

-- =============================================================================
-- 샘플 데이터
-- =============================================================================

-- profiles
INSERT INTO profiles (user_id, segment, interests, risk_score, created_at) VALUES
('user-1', '["seg1","seg2"]', '["news","sports"]', 0.1, CURRENT_TIMESTAMP),
('unknown', '[]', '[]', 0.5, CURRENT_TIMESTAMP);

-- campaign_placements (placement-1: 기존 mock과 동일 camp-1/creative-1, camp-2/creative-2)
INSERT INTO campaign_placements (placement_id, campaign_id, creative_id, status, budget, bid, target_conditions, created_at) VALUES
('placement-1', 'camp-1', 'creative-1', 'ACTIVE', 10000.0000, 2.5000, '{}', CURRENT_TIMESTAMP),
('placement-1', 'camp-2', 'creative-2', 'ACTIVE', 5000.0000, 1.8000, '{}', CURRENT_TIMESTAMP),
('placement-1', 'camp-1', 'creative-3', 'ACTIVE', 10000.0000, 2.0000, '{}', CURRENT_TIMESTAMP);

-- cap_allowance (openrtb + user-1 전체 허용, unknown도 허용)
INSERT INTO cap_allowance (client_id, user_id, campaign_id, remaining, created_at) VALUES
('openrtb', 'user-1', '*', 100, CURRENT_TIMESTAMP),
('openrtb', 'unknown', '*', 100, CURRENT_TIMESTAMP);

-- policies: openrtb 클라이언트용 (Decision 공개 API clientId)
INSERT INTO policies (client_id, filter_rules, ab_flags, timeout_config, active, created_at, updated_at) VALUES
('openrtb', '{"minBid":0.1}', '{"useNewScoring":true}', '{"default":30}', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
