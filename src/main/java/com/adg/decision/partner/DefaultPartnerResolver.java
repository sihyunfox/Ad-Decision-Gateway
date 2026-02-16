package com.adg.decision.partner;

import com.adg.shared.dto.openrtb.BidRequest;
import com.adg.shared.dto.openrtb.Source;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 파트너 ID 결정: 1차 HTTP 헤더, 2차 source.ext.partner_id, 없으면 default.
 */
@Component
public class DefaultPartnerResolver implements PartnerResolver {

    private static final String DEFAULT_PARTNER_ID = "default";
    private static final String SOURCE_EXT_PARTNER_KEY = "partner_id";

    @Value("${app.partner.header-name:X-Partner-Id}")
    private String partnerHeaderName;

    @Override
    public String resolve(HttpServletRequest httpRequest, BidRequest bidRequest) {
        if (httpRequest != null) {
            String fromHeader = httpRequest.getHeader(partnerHeaderName);
            if (fromHeader != null && !fromHeader.isBlank()) {
                return fromHeader.trim();
            }
        }
        if (bidRequest != null && bidRequest.getSource() != null) {
            Source source = bidRequest.getSource();
            Map<String, Object> ext = source.getExt();
            if (ext != null && ext.containsKey(SOURCE_EXT_PARTNER_KEY)) {
                Object v = ext.get(SOURCE_EXT_PARTNER_KEY);
                if (v != null) {
                    String s = v.toString();
                    if (!s.isBlank()) return s.trim();
                }
            }
        }
        if (bidRequest != null && bidRequest.getExt() != null && bidRequest.getExt().containsKey(SOURCE_EXT_PARTNER_KEY)) {
            Object v = bidRequest.getExt().get(SOURCE_EXT_PARTNER_KEY);
            if (v != null) {
                String s = v.toString();
                if (!s.isBlank()) return s.trim();
            }
        }
        return DEFAULT_PARTNER_ID;
    }
}
