# ADR 003: H2 local-first storage

## Status
Accepted

## Context
Most development and CI should run without external DB. Production may use Postgres/MySQL.

## Decision
Use H2 (file or in-memory) as default. Schema managed by Flyway. JPA entities and repositories are written so that switching to another RDBMS is a configuration and driver change; no schema logic in code.

## Consequences
- Local run and tests use H2. Optional Testcontainers for Postgres in CI.
- Port/Adapter keeps persistence behind interfaces where needed.
