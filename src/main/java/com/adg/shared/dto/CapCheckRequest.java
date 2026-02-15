package com.adg.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cap(노출/예산 상한) 검사 요청 DTO.
 * <p>
 * Decision 파이프라인에서 특정 유저·캠페인·클라이언트 조합에 대한 노출 허용 여부를 조회할 때 사용.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapCheckRequest {

    private String userId;
    private String campaignId;
    private String clientId;
}
