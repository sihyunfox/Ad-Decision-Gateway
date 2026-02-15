# OpenRTB DSP 재검증 및 Event API 재설계 계획

## 1. 목표
- **Event API**: 노출(impression), 클릭(click), nurl, burl, lurl 5종. URL 수신 → 유효성검사 → 어뷰징 적용 → 로깅. 구조만 잡고 각 단계는 **로깅만** 수행. 요청/응답 동일(URL 전달).
- **인증**: DSP 공개 API(Decision, Event) **인증 없음**. Admin만 인증 유지.
- **정리**: 기존 Event의 eventId/decisionId/clientId 기반 수집·idempotency 제거. 이벤트는 EventQueuePort(이벤트 큐) 적재 후 Drainer가 파일 기록(현재 구현). 요구사항.md 갱신.

---

## 2. 작업 목록

### 2.1 Event DTO 및 AuthFilter
- **EventUrlRequest**: `url` (필수, @NotBlank)
- **EventUrlResponse**: `url` (요청과 동일 반환)
- **AuthFilter**: `/v1/events` 를 public 경로로 추가, 해당 시 `ATTR_CLIENT_ID = "openrtb"` 설정

### 2.2 Event 파이프라인(구조만, 로깅만)
- **단계**: 유효성검사 → 어뷰징 → 로깅
- **구현**: `EventValidationStage`, `EventAntiAbuseStage`, `EventLoggingStage` 인터페이스 + Default 구현체. 각 구현체는 `log.info(...)` 만 수행.
- **EventPipeline** 또는 **EventService**에서 위 3단계 순차 호출.

### 2.3 event_url_log 테이블 및 영속성
- **V4 마이그레이션**: `event_url_log(id BIGINT PK, event_type VARCHAR(32), url VARCHAR(2048), created_at TIMESTAMP)`
- **EventUrlLogEntity**, **EventUrlLogRepository** 생성. 로깅 단계에서 저장(선택).

### 2.4 EventController · EventService
- **Controller**: `POST /v1/events/impression`, `/click`, `/nurl`, `/burl`, `/lurl` — Body: `EventUrlRequest`, Return: `EventUrlResponse` (동일 url). 인증 없음.
- **EventService**: `handleEvent(url, eventType)` — 파이프라인 3단계 실행 후 필요 시 event_url_log 저장.

### 2.5 기존 Event 제거·테스트 수정
- **제거/대체**: EventController·EventService에서 `EventRequest`, `EventEntity`, `EventRepository`(구) 사용 제거. 이벤트는 EventQueuePort(이벤트 큐) 적재로 대체됨.
- **테스트**: EventControllerTest를 EventUrlRequest/EventUrlResponse 기반으로, 5개 엔드포인트 각각 검증.

### 2.6 요구사항.md 업데이트
- **§3**: DSP 공개 API(Decision, Event) 인증 없음. Admin만 인증.
- **§5.2**: Event API 5종(노출/클릭/nurl/burl/lurl), URL 수신, 유효성검사-어뷰징-로깅, 요청/응답 동일.
- **§6.3**: event_url_log 테이블 명시.

---

## 3. 작업 순서
1. EventUrlRequest, EventUrlResponse DTO 추가 + AuthFilter `/v1/events` public
2. Event 파이프라인 3단계(인터페이스+Default, 로깅만) + EventPipeline 또는 Service에서 호출
3. V4 event_url_log + EventUrlLogEntity + EventUrlLogRepository
4. EventService 재작성 + EventController 5종 엔드포인트
5. 기존 EventRequest/EventEntity/EventRepository 참조 제거, EventControllerTest 수정
6. 요구사항.md 수정

---

## 4. 유지하는 것
- Decision API: 현재 그대로 (OpenRTB BidRequest/BidResponse, 인증 없음)
- Admin API: 인증 유지 (ATTR_IS_ADMIN)
- 기존 `events` 테이블: V1/V2 스키마 유지(deprecated). 새 로그는 `event_url_log` 사용
