package com.adg.shared.dto.openrtb;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * OpenRTB 2.x Bid Response (DSP 응답).
 * <p>id는 BidRequest.id와 동일. seatbid[].bid[]에 impid, price, crid, adid, nurl 등 포함.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BidResponse {

    /** 필수. BidRequest.id와 동일 */
    private String id;
    /** Bidder가 부여한 입찰 ID */
    private String bidid;
    /** 통화 (BidRequest.cur 기준) */
    private String cur;
    /** 필수. SeatBid 목록 */
    private List<SeatBid> seatbid;
    /** OpenRTB 2.x 확장 (파트너별 커스텀) */
    private Map<String, Object> ext;
}
