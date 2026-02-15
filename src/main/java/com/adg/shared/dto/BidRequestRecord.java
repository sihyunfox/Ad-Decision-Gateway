package com.adg.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 광고 요청(Bid Request) 수집 시 저장용 레코드 DTO.
 * BidRequestPort.save()로 전달.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BidRequestRecord {

    private String requestId;
    private String rawJson;
    private String impIds;
    private String siteId;
    private String appId;
    private String publisherId;
}
