package com.adg.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 정책(policy) 한 건을 나타내는 DTO.
 * <p>
 * Application 계층 내부(Decision 시 정책 조회) 및 Admin API 응답·수정 요청에 사용.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyDto {

    private Long id;
    private String clientId;
    private String filterRules;
    private String abFlags;
    private String timeoutConfig;
    private boolean active;
}
