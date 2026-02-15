# Ad Decision Gateway (ADG) — 에이전트 오케스트레이션

## 목적
이 문서는 ADG 프로젝트 구현 시 **오케스트레이터**와 **서브에이전트**의 역할·위임 규칙을 정의한다.  
요구사항(`요구사항.md`)의 기능/비기능 요구를 TDD, 클린 아키텍처, 성능 목표에 맞게 나누어 담당한다.

---

## 오케스트레이터: @adg-orchestrator

**역할**: 전체 작업 분해, 서브에이전트 위임, 아키텍처·품질 원칙 준수 감시.

- 사용자 요청을 받으면 **요구사항.md**와 **기술 스택**을 기준으로 작업 단위를 나눈다.
- 각 단위를 해당 **서브에이전트**에게 위임한다. (예: "Decision API 구현" → @adg-decision)
- 서브에이전트 결과를 종합하고, **도메인 경계·포트/어댑터·테스트·성능** 원칙이 지켜졌는지 검토한다.
- 크로스 커팅(인증, 로깅, Resilience)은 오케스트레이터가 조율하거나 해당 전문 에이전트에 명시적으로 위임한다.

**호출 예시**:  
"Decision API 구현해줘" → @adg-orchestrator가 계획 수립 후 @adg-decision 위임 + 필요 시 @adg-resilience, @adg-domain-tests 참여 지시.

---

## 서브에이전트 목록 및 담당 영역

| @멘션 | 담당 영역 | 핵심 책임 |
|--------|-----------|------------|
| **@adg-orchestrator** | 전체 조율 | 작업 분해, 위임, 원칙 준수 검토 |
| **@adg-decision** | Decision API | POST /v1/decision, downstream 병렬 호출, 필터/스코어링, fallback |
| **@adg-events** | Event API | impression/click 수집, 이벤트 큐(EventQueuePort) 적재 |
| **@adg-policy-admin** | Policy/Admin | 정책 조회/업데이트, audit log, feature flag |
| **@adg-resilience** | 안정성 | Timeout, Retry, Circuit Breaker, Bulkhead, Fallback (Resilience4j) |
| **@adg-observability** | 관측성 | JSON 로그, 메트릭, health, (선택) tracing |
| **@adg-async-worker** | 비동기 처리 | 이벤트 큐 drain(EventQueueDrainer), 파일 기록; 확장 시 Outbox/Worker |
| **@adg-domain-tests** | 테스트 | TDD, 단위/슬라이스/통합/장애 시나리오 테스트 |
| **@adg-docs** | 문서화 | OpenAPI, ADR, Runbook, Mermaid 다이어그램 |

---

## 위임 규칙

1. **도메인 경계**: 패키지는 **Vertical Slice + 공유(shared)** 구조. Decision/Event/Admin은 각각 `decision.*`, `event.*`, `admin.*` 슬라이스로 @adg-decision, @adg-events, @adg-policy-admin이 담당. 공유 DTO·포트·어댑터는 `shared.*`에 두고, 오케스트레이터가 패키지 구조를 정한 뒤 해당 에이전트에 전달.
2. **인프라·공통**: Mock 서비스 클라이언트, DB 스키마(Flyway/Liquibase), 인증/인가는 오케스트레이터가 결정 후 @adg-resilience, @adg-observability 등과 조합.
3. **테스트**: 기능 구현 시 @adg-domain-tests의 TDD 규칙을 따르고, 필요 시 해당 에이전트에게 "이 기능에 대한 테스트 케이스/슬라이스 테스트 작성"을 위임.
4. **성능**: Avg <50ms, P95 <100ms, 200 rps 부하 요구는 모든 API/Worker 관련 작업에 적용. 새 코드는 @adg-observability의 메트릭/로깅 지침을 준수.

---

## 참조 문서

- **요구사항.md**: 기능/비기능/기술 스택 정의
- **.cursor/rules/**: TDD, 클린 아키텍처, 성능 규칙 (항상 적용 또는 해당 파일 패턴)
- **.cursor/agents/*.md**: 각 서브에이전트 상세 지침
