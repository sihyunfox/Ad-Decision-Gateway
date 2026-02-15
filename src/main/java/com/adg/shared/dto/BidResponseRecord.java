package com.adg.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 광고 응답(Bid Response) 수집 시 저장용 레코드 DTO.
 * BidResponsePort.save()로 전달.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BidResponseRecord {

    private String requestId;
    private String bidResponseId;
    private String seat;
    private String bidImpid;
    private java.math.BigDecimal bidPrice;
    private String bidCrid;
    private String bidAdid;
    private String rawJson;
}
