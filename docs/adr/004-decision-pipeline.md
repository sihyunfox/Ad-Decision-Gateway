# ADR 004: Decision 4단계 파이프라인

## Status
Accepted

## Context
광고 요청 처리가 요청 유효성 검증 → 광고 필터링 → 광고 선택 → 노출의 순서로 이루어지며, 단계 추가·교체가 가능한 확장 가능한 구조가 필요하다.

## Decision
Decision 처리를 **4단계 파이프라인**으로 분리한다.

1. **RequestValidationStage**: 요청 유효성 검증. 실패 시 예외.
2. **AdFilteringStage**: 후보 광고 필터링 (정책/Cap/상태). context의 candidates → filteredCandidates.
3. **AdSelectionStage**: 광고 선택(우승자 선정). context의 filteredCandidates → winner.
4. **ExposureStage**: 노출 준비. 스켈레톤에서는 시그니처만 두고 확장 시 응답/기록 준비 등 추가.

- **AdDecisionPipelineContext**: 단계 간 공유 컨텍스트 (requestId, decisionId, request, candidates, capResponse, filteredCandidates, winner, fallbackUsed 등).
- **AdDecisionPipeline**: 위 4개 Stage를 순서대로 실행하는 오케스트레이터. 각 Stage는 인터페이스로 두어 구현체 교체·추가 가능.
- DecisionService는 downstream 병렬 호출 후 context를 채우고 `pipeline.run(context)`를 호출한 뒤, context 결과로 기존 저장·응답 로직을 수행.

스켈레톤에서는 각 단계를 최소 구현(Default*): Validation은 필수 필드 체크, Filtering/Selection은 기존 CampaignFilter/WinnerSelector 위임, Exposure는 no-op.

## Consequences
- 단계별 책임이 명확해지고, 새 단계 추가 또는 구현체 교체가 쉬워진다.
- 파이프라인과 무관한 부수 작업(데이터 로드, 저장, 이벤트 큐 적재, 메트릭)은 DecisionService에 두어 단계는 순수하게 검증·필터·선택·노출 준비에 집중할 수 있다.
