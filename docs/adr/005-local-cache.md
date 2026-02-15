# ADR 005: 로컬 캐시 (Caffeine) for 광고/소재 조회

## Status
Accepted

## Context
광고/소재 등 DB 조회 빈도가 높을 수 있어, 조회 성능을 위해 로컬 캐시를 적용하고자 한다.

## Decision
- **Spring Cache + Caffeine**을 사용한다.
- **config.CacheConfig**: `@EnableCaching`, Caffeine 기반 `CacheManager` 빈. 캐시 이름 `creatives`, maximumSize 1000, expireAfterWrite 10분.
- **적용 대상**: CreativeService.getByCreativeId(creativeId). `@Cacheable(value = "creatives", key = "#creativeId", unless = "#result == null")`.
- **캐시 무효화**: CreativeService.create 시 `@CacheEvict(value = "creatives", key = "#dto.creativeId")`. update 시 `@CacheEvict(value = "creatives", allEntries = true)` (구현 단순화).

추가 확장 시 PolicyService.listActivePolicies() 등에 `policies` 캐시를 도입할 수 있다.

## Consequences
- 소재 단건 조회 시 DB 부하 감소 및 응답 시간 개선.
- 소재 생성/수정 시 캐시 무효화로 일관성 유지.
- 단일 노드 로컬 캐시이므로 분산 환경에서는 별도 캐시 전략(Redis 등) 검토 필요.
