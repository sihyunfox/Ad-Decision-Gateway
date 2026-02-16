package com.adg.shared.dto.openrtb;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * OpenRTB 2.x Bid Request (DSP 수신).
 * <p>필수: id(요청 ID), imp[](노출 단위). imp[].id가 placementId로 사용됨.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BidRequest {

    /** 필수. 요청 ID (Exchange/SSP 부여) */
    @NotBlank(message = "id is required")
    private String id;

    /** 필수. 노출 단위 목록 */
    @NotEmpty(message = "imp is required")
    @Valid
    private List<Imp> imp;

    private Site site;
    private App app;
    private Source source;
    private Device device;
    private User user;

    /** 입찰 방식 (1=1차가격, 2=2차가격, 3=정해진가) */
    private Integer at;
    /** 응답 제한 시간(ms) */
    private Integer tmax;
    /** 허용 통화 */
    private List<String> cur;
    /** 허용 seat(광고주) ID */
    private List<String> wseat;
    /** OpenRTB 2.x 확장 (파트너별 커스텀) */
    private Map<String, Object> ext;
}
