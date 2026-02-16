package com.adg.decision.partner;

import com.adg.shared.dto.openrtb.Bid;
import com.adg.shared.dto.openrtb.BidResponse;
import com.adg.shared.dto.openrtb.SeatBid;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 파트너별 응답 ext 주입. 정책의 responseExt를 BidResponse.ext, SeatBid[].ext, Bid[].ext에 병합.
 */
@Component
public class PartnerExtResponseEnricher {

    private final PartnerExtPolicyRegistry registry;

    public PartnerExtResponseEnricher(PartnerExtPolicyRegistry registry) {
        this.registry = registry;
    }

    /**
     * 파트너 정책에 따라 response.ext, seatbid[].ext, seatbid[].bid[].ext에 값을 주입.
     * 기존 ext가 있으면 병합(정책 값이 우선).
     */
    public BidResponse enrich(BidResponse response, String partnerId) {
        if (response == null) return response;
        PartnerExtPolicy policy = registry.getPolicy(partnerId);
        Map<String, Object> responseExt = policy.getResponseExt();
        if (responseExt == null || responseExt.isEmpty()) {
            return response;
        }

        if (response.getExt() == null) {
            response.setExt(new HashMap<>(responseExt));
        } else {
            Map<String, Object> merged = new HashMap<>(response.getExt());
            merged.putAll(responseExt);
            response.setExt(merged);
        }

        List<SeatBid> seatbids = response.getSeatbid();
        if (seatbids != null) {
            for (SeatBid seatBid : seatbids) {
                if (seatBid.getExt() == null) {
                    seatBid.setExt(new HashMap<>(responseExt));
                } else {
                    Map<String, Object> merged = new HashMap<>(seatBid.getExt());
                    merged.putAll(responseExt);
                    seatBid.setExt(merged);
                }
                if (seatBid.getBid() != null) {
                    for (Bid bid : seatBid.getBid()) {
                        if (bid.getExt() == null) {
                            bid.setExt(new HashMap<>(responseExt));
                        } else {
                            Map<String, Object> merged = new HashMap<>(bid.getExt());
                            merged.putAll(responseExt);
                            bid.setExt(merged);
                        }
                    }
                }
            }
        }
        return response;
    }
}
