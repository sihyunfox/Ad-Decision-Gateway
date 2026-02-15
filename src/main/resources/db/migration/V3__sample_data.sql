-- =============================================================================
-- 테스트/스켈레톤용 샘플 데이터 (로컬 실행 시 Decision·Event·Admin API 검증용)
-- =============================================================================

-- 1) 퍼블리셔
INSERT INTO publishers (publisher_id, name, domain, ext_json, created_at, updated_at) VALUES
('pub-1', 'Sample Media Corp', 'sample-media.example.com', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('pub-2', 'Test Publisher', 'test-pub.example.com', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 2) 사이트 (publisher_id: 1, 2는 위 INSERT 후 자동 부여된 id)
INSERT INTO sites (site_id, publisher_id, name, domain, page, ref, ext_json, created_at, updated_at) VALUES
('site-1', 1, 'Sample News', 'news.sample-media.example.com', NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('site-2', 2, 'Test Blog', 'blog.test-pub.example.com', NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3) 앱
INSERT INTO apps (app_id, publisher_id, name, bundle, domain, storeurl, ver, ext_json, created_at, updated_at) VALUES
('app-1', 1, 'Sample News App', 'com.sample.news', 'sample-media.example.com', 'https://play.example.com/sample-news', '1.0', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 4) 광고 소재 (Mock Campaign의 creative-1, creative-2와 매칭 + house + 추가 소재)
INSERT INTO creatives (creative_id, campaign_id, name, format, width, height, mime_type, adm_snippet, landing_url, status, created_at, updated_at) VALUES
('creative-1', 'camp-1', '메인 배너 300x250', 'banner', 300, 250, 'text/html', '<div id="ad-300x250">Sample Banner</div>', 'https://landing.example.com/camp1', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('creative-2', 'camp-2', '사이드 배너 160x600', 'banner', 160, 600, 'text/html', '<div id="ad-160x600">Side Banner</div>', 'https://landing.example.com/camp2', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('house-default', 'house', 'House 광고 기본 소재', 'banner', 300, 250, 'text/html', '<div>House Ad</div>', 'https://example.com', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('creative-3', 'camp-1', '동일 캠페인 추가 소재 728x90', 'banner', 728, 90, 'text/html', '<div id="ad-728x90">Leaderboard</div>', 'https://landing.example.com/camp1', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 5) 정책 (Admin 정책 조회 및 Decision 시 downstream 연동용)
INSERT INTO policies (client_id, filter_rules, ab_flags, timeout_config, active, created_at, updated_at) VALUES
('default', '{"minBid":0.1}', '{"useNewScoring":true}', '{"default":30}', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
