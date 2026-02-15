package com.adg.shared.port;

import com.adg.shared.dto.BidResponseRecord;

/**
 * 광고 응답(Bid Response) 수집 저장을 위한 포트.
 */
public interface BidResponsePort {

    void save(BidResponseRecord record);
}
