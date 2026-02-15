package com.adg.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cap(노출/예산 상한) 검사 응답 DTO.
 * <p>
 * allow=true이면 해당 조합으로 노출 가능. remaining은 남은 노출/예산 등 (선택적).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapCheckResponse {

    private boolean allow;
    private int remaining;
}
