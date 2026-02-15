# 테스트 코드 vs 현재 요구사항 검토

## 요구사항 요약 (기준)
- **Decision API**: POST /v1/decision, OpenRTB BidRequest/BidResponse, **인증 없음**. 예외: 400(validation), 503(fallback 정책).
- **Event API**: POST /v1/events/impression, /click, /nurl, /burl, /lurl, Request/Response `{ "url" }`, **인증 없음**. 예외: 400(validation, url 필수).
- **Admin API**: 인증 필수(ATTR_IS_ADMIN), 403 when not admin.

---

## 1. DecisionControllerTest — 일치

| 항목 | 요구사항 | 테스트 | 판정 |
|------|----------|--------|------|
| 요청 형식 | OpenRTB BidRequest (id, imp[]) | BidRequest.builder().id("req-1").imp(List.of(Imp.builder().id("p1").build())) | OK |
| 응답 형식 | BidResponse (id, bidid, seatbid[].bid[]) | jsonPath $.id, $.bidid, $.seatbid[0].bid[0].id/impid/crid | OK |
| 인증 | 없음 | X-API-Key 헤더 없음, addFilters=false 시 clientId=openrtb mock | OK |
| Mock 서비스 | decide(bidRequest, clientId) | when(decisionService.decide(any(BidRequest.class), eq("openrtb"))) | OK |

**보강 반영**: `decide_returns400WhenIdBlank`, `decide_returns400WhenImpEmpty` 추가 — id 빈 문자열 또는 imp 빈 배열 시 400 검증.

---

## 2. DecisionIntegrationTest — 일치

| 항목 | 요구사항 | 테스트 | 판정 |
|------|----------|--------|------|
| 인증 없음 | Decision 인증 없음 | X-API-Key 헤더 없음, TestPropertySource(api-keys 등) 제거됨 | OK |
| 요청/응답 | BidRequest → BidResponse | OpenRTB BidRequest body, $.id, $.seatbid[0].bid[0].impid/price/crid 검증 | OK |

---

## 3. DecisionFallbackIntegrationTest — 일치

| 항목 | 요구사항 | 테스트 | 판정 |
|------|----------|--------|------|
| Fallback 시 200 + BidResponse | 전체 실패 시 fallback 응답 | 200, seatbid, bid[0].impid, crid 존재 | OK |
| 인증 없음 | 동일 | 헤더 없음 | OK |

---

## 4. EventControllerTest — 일치

| 항목 | 요구사항 | 테스트 | 판정 |
|------|----------|--------|------|
| 5종 엔드포인트 | impression, click, nurl, burl, lurl | impression/click/nurl/burl/lurl 각각 @Test | OK |
| Request/Response | { "url" } 동일 | EventUrlRequest.url, EventUrlResponse.url, jsonPath("$.url") | OK |
| 인증 없음 | Event 인증 없음 | X-API-Key·ATTR_CLIENT_ID 설정 없음 | OK |
| 서비스 시그니처 | handleEvent(request, eventType) | handleEvent(any(EventUrlRequest.class), eq("impression")) 등 | OK |

**보강 반영**: `impression_returns400WhenUrlBlank`, `click_returns400WhenUrlMissing` 추가 — url 빈 문자열 또는 url 필드 누락 시 400 검증.

---

## 5. AdminPolicyControllerTest — 일치

| 항목 | 요구사항 | 테스트 | 판정 |
|------|----------|--------|------|
| Admin 인증 | Admin API 인증 필수 | ATTR_IS_ADMIN=true, ATTR_CLIENT_ID=admin 시 200 | OK |
| 비인증 시 403 | Admin 아닐 때 403 | ATTR_IS_ADMIN=false 시 403 | OK |

---

## 6. 기타 테스트 (도메인·인프라)

| 테스트 | 역할 | 요구사항과의 관계 | 판정 |
|--------|------|-------------------|------|
| WinnerSelectorTest | Decision 도메인(최고 입찰 선정) | 파이프라인 내부 로직 | 유지 적합 |
| CampaignFilterTest | Decision 도메인(캠페인 필터) | 파이프라인 내부 로직 | 유지 적합 |
| (이벤트 큐) | EventQueuePort 적재·Drainer 파일 기록 | Decision·Event·Policy 등 이벤트 큐 사용 | 코드 기준: shared.adapter.eventqueue, 단위 테스트는 슬라이스/통합에서 검증 |

---

## 7. 요약

- **현재 테스트는 요구사항과 일치**합니다.  
  - Decision: OpenRTB BidRequest/BidResponse, 인증 없음 반영.  
  - Event: 5종 URL 기반, Request/Response 동일, 인증 없음 반영.  
  - Admin: 인증 필수 및 403 반영.

- **선택적 보완** (요구사항 문서의 “예외” 조항을 테스트로 커버하려면):
  1. **DecisionControllerTest**: Bean Validation 실패 시 400 테스트 1건 (예: id 누락 또는 imp 빈 배열).
  2. **EventControllerTest**: url 필수 검증 실패 시 400 테스트 1건 (예: body `{}` 또는 `{"url":""}`). → **보강 완료**: 위 두 항목 반영됨.

- **Event E2E**: Event 5종에 대한 통합 테스트(실제 EventService + 파이프라인 + event_url_log 저장)는 요구사항에 명시되어 있지 않으나, 필요 시 `DecisionIntegrationTest`와 유사한 형태로 추가 가능.
