package com.adg.decision.partner;

import com.adg.shared.dto.openrtb.BidRequest;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 요청에서 파트너(Exchange/DSP) ID를 결정.
 * <p>1차: HTTP 헤더. 2차: BidRequest.source.ext 내 agreed 키. 미지정 시 기본값.</p>
 */
public interface PartnerResolver {

    /**
     * 요청·바디에서 파트너 ID 결정.
     * @param httpRequest HTTP 요청 (헤더)
     * @param bidRequest OpenRTB Bid Request (source.ext 등)
     * @return 파트너 ID (null이면 안 됨; 기본값 "default" 반환)
     */
    String resolve(HttpServletRequest httpRequest, BidRequest bidRequest);
}
