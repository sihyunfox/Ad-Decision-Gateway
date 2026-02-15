package com.adg.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Downstream Profile 서비스에서 반환하는 유저 프로필 DTO.
 * <p>
 * Decision 파이프라인에서 타겟팅·필터·스코어링에 활용 (세그먼트, 관심사, 리스크 스코어).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

    private String userId;
    private List<String> segment;
    private List<String> interests;
    private Double riskScore;
}
