# ADR 002: Resilience4j for downstream calls

## Status
Accepted

## Context
Profile, Campaign, Policy, and Cap calls can fail or be slow. We need timeout, retry, circuit breaker, and bulkhead without blocking the main thread indefinitely.

## Decision
Use Resilience4j with Spring Boot 3. Apply CircuitBreaker, Retry, and Bulkhead to each dependency adapter. RestTemplate read/connect timeouts provide request-level timeout. Fallback returns empty/default when circuit is open or all retries fail.

## Consequences
- Configurable per dependency in application.yml.
- Health indicators and metrics exposed via Actuator.
- Fallback behavior (e.g. house ad) when all downstream calls fail.
