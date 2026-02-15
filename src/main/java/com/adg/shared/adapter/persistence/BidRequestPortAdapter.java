package com.adg.shared.adapter.persistence;

import com.adg.shared.dto.BidRequestRecord;
import com.adg.shared.port.BidRequestPort;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * BidRequestPort 구현체. BidRequestRecord를 bid_requests 테이블에 저장한다.
 */
@Component
public class BidRequestPortAdapter implements BidRequestPort {

    private final BidRequestRepository repository;

    public BidRequestPortAdapter(BidRequestRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(BidRequestRecord record) {
        BidRequestEntity entity = BidRequestEntity.builder()
                .requestId(record.getRequestId())
                .rawJson(record.getRawJson())
                .impIds(record.getImpIds())
                .siteId(record.getSiteId())
                .appId(record.getAppId())
                .publisherId(record.getPublisherId())
                .receivedAt(Instant.now())
                .build();
        repository.save(entity);
    }
}
