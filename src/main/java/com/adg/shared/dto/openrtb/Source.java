package com.adg.shared.dto.openrtb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * OpenRTB 2.6 Source 객체 (BidRequest 내). 공급 체인/거래소 정보.
 * <p>파트너 식별 시 source.ext.partner_id 등으로 fallback 가능.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Source {

    private Integer fd;
    private String tid;
    private String pchain;
    /** OpenRTB 2.x 확장 (partner_id 등) */
    private Map<String, Object> ext;
}
