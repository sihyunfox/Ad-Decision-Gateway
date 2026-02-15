package com.adg.decision.app;

import com.adg.shared.dto.DecisionRequest;
import com.adg.shared.dto.openrtb.BidRequest;
import com.adg.shared.dto.openrtb.BidResponse;
import com.adg.shared.dto.openrtb.Bid;
import com.adg.shared.dto.openrtb.SeatBid;
import com.adg.shared.dto.CampaignItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * OpenRTB BidRequest/BidResponse와 내부 DecisionRequest 및 파이프라인 결과 간 매핑.
 * <p>toDecisionRequest: API 수신 BidRequest → 파이프라인용 DecisionRequest.
 * toBidResponse: 파이프라인 결과(winner, decisionId 등) → API 응답 BidResponse.</p>
 */
public final class OpenRtbMapper {

    private OpenRtbMapper() {
    }

    /**
     * OpenRTB BidRequest를 파이프라인/다운스트림용 DecisionRequest로 변환.
     */
    public static DecisionRequest toDecisionRequest(BidRequest bidRequest) {
        String requestId = bidRequest.getId();
        String placementId = null;
        Integer w = null;
        Integer h = null;
        if (bidRequest.getImp() != null && !bidRequest.getImp().isEmpty()) {
            var firstImp = bidRequest.getImp().get(0);
            placementId = firstImp.getId();
            if (firstImp.getBanner() != null) {
                w = firstImp.getBanner().getW();
                h = firstImp.getBanner().getH();
            }
        }
        if (placementId == null) {
            placementId = "unknown";
        }

        String siteId = bidRequest.getSite() != null ? bidRequest.getSite().getId() : null;
        String appId = bidRequest.getApp() != null ? bidRequest.getApp().getId() : null;

        DecisionRequest.DeviceInfo deviceInfo = null;
        if (bidRequest.getDevice() != null) {
            deviceInfo = new DecisionRequest.DeviceInfo(
                    bidRequest.getDevice().getOs(),
                    bidRequest.getDevice().getModel(),
                    bidRequest.getDevice().getIfa(),
                    bidRequest.getDevice().getGaid()
            );
        }

        DecisionRequest.UserInfo userInfo = null;
        if (bidRequest.getUser() != null) {
            userInfo = new DecisionRequest.UserInfo(
                    bidRequest.getUser().getId(),
                    null
            );
        }

        DecisionRequest.GeoInfo geoInfo = null;
        if (bidRequest.getDevice() != null && bidRequest.getDevice().getGeo() != null) {
            var geo = bidRequest.getDevice().getGeo();
            geoInfo = new DecisionRequest.GeoInfo(geo.getCountry(), geo.getRegion());
        }

        DecisionRequest.AdSize adSize = null;
        if (w != null || h != null) {
            adSize = new DecisionRequest.AdSize(w, h);
        }

        return DecisionRequest.builder()
                .requestId(requestId)
                .clientId(null) // Controller에서 auth로 채움
                .placementId(placementId)
                .appId(appId)
                .siteId(siteId)
                .device(deviceInfo)
                .user(userInfo)
                .geo(geoInfo)
                .adSize(adSize)
                .debug(false)
                .build();
    }

    /**
     * 파이프라인 결과(decisionId, requestId, winner, fallbackUsed 등)를 OpenRTB BidResponse로 변환.
     */
    public static BidResponse toBidResponse(
            String bidRequestId,
            String decisionId,
            String clientId,
            String impid,
            CampaignItem winner,
            boolean fallbackUsed,
            String trackingUrl
    ) {
        BigDecimal price = winner != null && winner.getBid() != null ? winner.getBid() : java.math.BigDecimal.ZERO;
        Bid bid = Bid.builder()
                .id(decisionId)
                .impid(impid)
                .price(price)
                .adid(winner != null ? winner.getCampaignId() : null)
                .crid(winner != null ? winner.getCreativeId() : null)
                .nurl(trackingUrl)
                .adm(winner != null && winner.getCreativeId() != null
                        ? "<div data-crid=\"" + winner.getCreativeId() + "\"></div>"
                        : "<div>house</div>")
                .build();
        SeatBid seatBid = SeatBid.builder()
                .seat(clientId)
                .bid(List.of(bid))
                .build();
        return BidResponse.builder()
                .id(bidRequestId)
                .bidid(decisionId)
                .cur("USD")
                .seatbid(List.of(seatBid))
                .build();
    }
}
