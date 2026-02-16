package com.adg.shared.dto.openrtb;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * OpenRTB 2.x SeatBid 객체.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SeatBid {

    /** Seat(광고주) ID */
    private String seat;
    /** 필수. Bid 목록 */
    private List<Bid> bid;
    /** OpenRTB 2.x 확장 (파트너별 커스텀) */
    private Map<String, Object> ext;
}
