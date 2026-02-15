# ADR 001: Outbox pattern for event publishing

## Status
Accepted (참조용; 현재 구현은 아래 "현재 구현" 참고)

## Context
Decision, Event, and Policy flows must trigger downstream processing (e.g. analytics, Kafka) without losing events or breaking transaction consistency.

## Decision
Use an outbox table: within the same transaction as the business write, insert a row with event type and payload. A separate worker polls PENDING rows and publishes them (or handles them). On success, mark SENT; on failure, schedule retry with backoff.

## Consequences
- At-least-once delivery; consumers must be idempotent.
- Same DB as source of truth; no dual-write inconsistency.
- Worker adds operational component (poll interval, retry config, monitoring).

## 현재 구현 (코드 기준)
DB outbox 테이블·폴링 Worker는 사용하지 않는다. 대신 **인메모리 이벤트 큐**(EventQueuePort → InMemoryEventQueueAdapter)에 적재하고, **EventQueueDrainer**가 주기적으로 drain 하여 **파일(NDJSON)** 에 기록한다. Graceful shutdown 시 EventQueueShutdownHandler가 스케줄 중단 후 잔량을 파일로 drain. 패키지: `shared.adapter.eventqueue`. 확장 시 ADR 001의 outbox 테이블·Worker 도입 가능.
