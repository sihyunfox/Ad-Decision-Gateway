package com.adg.decision.pipeline;

import com.adg.shared.dto.CampaignItem;
import com.adg.shared.dto.CapCheckResponse;
import com.adg.shared.dto.DecisionRequest;
import com.adg.shared.dto.DecisionResponse;
import com.adg.shared.dto.PolicyResponse;
import com.adg.shared.dto.ProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 광고 의사결정 파이프라인 내 단계 간 공유 컨텍스트.
 * 각 단계가 컨텍스트를 수정하여 다음 단계로 전달한다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdDecisionPipelineContext {

    private String requestId;
    private String decisionId;
    private String traceId;
    private String clientId;
    private DecisionRequest request;

    /** downstream/DataLoad로 수집한 후보 광고 목록 */
    private List<CampaignItem> candidates;
    /** Cap 검사 응답 (필터링 시 사용) */
    private CapCheckResponse capResponse;
    /** 프로필 (DataLoad 단계에서 설정, 확장용) */
    private Optional<ProfileResponse> profile;
    /** 정책 목록 (DataLoad 단계에서 설정, 확장용) */
    private List<PolicyResponse> policies;

    /** 필터링 통과한 후보 목록 */
    private List<CampaignItem> filteredCandidates;
    /** 선정된 우승자 (없으면 empty) */
    private Optional<CampaignItem> winner;

    /** fallback(house ad) 사용 여부 */
    private boolean fallbackUsed;
    /** AI 엔진 점수 (campaignId:creativeId -> score). AiScoringStage에서 설정 */
    private Map<String, Double> aiScores;
    /** 최종 응답 (노출 단계 또는 오케스트레이터에서 설정) */
    private DecisionResponse response;
}
