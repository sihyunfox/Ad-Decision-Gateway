package com.adg.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin API에서 정책 조회 시 반환하는 응답 DTO.
 * <p>
 * id/active는 제외하고 클라이언트가 수정 가능한 설정만 노출.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyResponse {

    private String clientId;
    private String filterRules;
    private String abFlags;
    private String timeoutConfig;
}
