package com.adg.shared.dto.openrtb;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * OpenRTB 2.x Bid 객체 (SeatBid 내).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Bid {

    /** 입찰 ID (bidder 부여) */
    private String id;
    /** 필수. 해당 노출 단위 Imp ID */
    private String impid;
    /** 필수. CPM/CPC 가격 */
    private BigDecimal price;
    /** 광고 ID (campaign 등) */
    private String adid;
    /** 소재 ID (creative) */
    private String crid;
    /** Win notice URL */
    private String nurl;
    /** Ad markup (HTML/XML/JSON) */
    private String adm;
    /** 소재 미리보기 URL */
    private String iurl;
    /** 캠페인 ID */
    private String cid;
}
