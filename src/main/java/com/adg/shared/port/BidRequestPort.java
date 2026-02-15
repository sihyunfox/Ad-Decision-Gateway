package com.adg.shared.port;

import com.adg.shared.dto.BidRequestRecord;

/**
 * 광고 요청(Bid Request) 수집 저장을 위한 포트.
 */
public interface BidRequestPort {

    void save(BidRequestRecord record);
}
