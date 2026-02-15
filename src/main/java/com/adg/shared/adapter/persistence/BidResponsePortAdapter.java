package com.adg.shared.adapter.persistence;

import com.adg.shared.dto.BidResponseRecord;
import com.adg.shared.port.BidResponsePort;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * BidResponsePort 구현체. BidResponseRecord를 bid_responses 테이블에 저장한다.
 */
@Component
public class BidResponsePortAdapter implements BidResponsePort {

    private final BidResponseRepository repository;

    public BidResponsePortAdapter(BidResponseRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(BidResponseRecord record) {
        BidResponseEntity entity = BidResponseEntity.builder()
                .requestId(record.getRequestId())
                .bidResponseId(record.getBidResponseId())
                .seat(record.getSeat())
                .bidImpid(record.getBidImpid())
                .bidPrice(record.getBidPrice())
                .bidCrid(record.getBidCrid())
                .bidAdid(record.getBidAdid())
                .rawJson(record.getRawJson())
                .respondedAt(Instant.now())
                .build();
        repository.save(entity);
    }
}
