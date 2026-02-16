package com.adg.decision.validation;

import com.adg.shared.dto.openrtb.BidRequest;
import com.adg.shared.dto.openrtb.Imp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * OpenRTB 2.6 필수 파라미터 검증.
 * <p>BidRequest: id, imp(최소 1개). Imp: id, Banner/Video/Audio/Native 중 최소 1개. 권장: site 또는 app 중 하나.</p>
 * 실패 시 IllegalArgumentException (GlobalExceptionHandler에서 400).
 */
@Component
public class OpenRtb26Validator {

    /** site 또는 app 권장 검증 사용 여부 (false면 생략) */
    @Value("${app.openrtb.require-site-or-app:false}")
    private boolean requireSiteOrApp;

    /**
     * BidRequest가 OpenRTB 2.6 필수 요구사항을 만족하는지 검증. 실패 시 IllegalArgumentException.
     */
    public void validate(BidRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("BidRequest is required");
        }
        if (request.getImp() == null || request.getImp().isEmpty()) {
            throw new IllegalArgumentException("imp is required and must contain at least one element");
        }
        int i = 0;
        for (Imp imp : request.getImp()) {
            if (imp.getId() == null || imp.getId().isBlank()) {
                throw new IllegalArgumentException("imp[" + i + "].id is required");
            }
            boolean hasMedia = imp.getBanner() != null || imp.getVideo() != null
                    || imp.getAudio() != null || imp.getNativeAd() != null;
            if (!hasMedia) {
                throw new IllegalArgumentException("imp[" + i + "] must contain at least one of banner, video, audio, native");
            }
            i++;
        }
        if (requireSiteOrApp) {
            if ((request.getSite() == null && request.getApp() == null)) {
                throw new IllegalArgumentException("Either site or app is recommended");
            }
        }
    }
}
