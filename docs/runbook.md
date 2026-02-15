# ADG Runbook

## SLO / SLA (target)

- **Availability**: 99.9% (excluding planned maintenance).
- **Latency**: P50 &lt; 50 ms, P95 &lt; 100 ms for `/v1/decision` (OpenRTB Bid Request/Response; local/mock baseline).
- **Throughput**: Support 200+ rps per instance for decision + events.

## Alerts (suggested thresholds)

| Metric | Threshold | Action |
|--------|-----------|--------|
| Error rate (5m) | &gt; 5% | Check logs, dependency health, circuit breakers |
| P95 latency | &gt; 200 ms | Check DB, downstream latency, bulkhead saturation |
| 이벤트 큐 백로그 | adg.eventQueue.size &gt; 5000 (경고 임계값) | Drainer 지연·파일 I/O 확인; 필요 시 drain-interval-ms·drain-batch-size 조정 |
| Circuit OPEN | Any instance | Normal under failure; verify fallback and recovery |

## Restart / recovery

1. **Graceful shutdown**: Allow in-flight requests to complete; EventQueueShutdownHandler가 drainer 스케줄 중단 후 큐 잔량을 파일로 drain; then stop app.
2. **Start**: Flyway runs migrations; EventQueueDrainer 스케줄이 다시 주기 drain 수행.
3. **DB unreachable**: Health will report DOWN; fix connectivity or failover DB and restart.

## Log and metric analysis

- **traceId**: Correlate logs and metrics for a single request. Use `traceId` in JSON logs and in response header `X-Trace-Id`.
- **decisionId**: Link decision to events and 이벤트 큐 payload(파일 기록).
- **Metrics**: `adg.decision.*`, `adg.eventQueue.size`, `resilience4j.*`. Use Prometheus/Grafana for RED and dependency success/failure.

## Common issues

- **401 on /v1/events or /v1/admin**: Missing or invalid `X-API-Key` (또는 Admin Key). Check `app.auth.api-keys` / `app.auth.admin-key`. (/v1/decision은 인증 없음.)
- **All decisions fallback**: Downstream (mock or real) failing or timing out. Check circuit breaker state and dependency URLs/timeouts.
- **이벤트 큐가 drain 되지 않음**: EventQueueDrainer 스케줄 동작 확인; `app.event-queue.drain-interval-ms`, `drain-batch-size` 및 파일 경로(`event-file`)·디스크 여유 확인.
