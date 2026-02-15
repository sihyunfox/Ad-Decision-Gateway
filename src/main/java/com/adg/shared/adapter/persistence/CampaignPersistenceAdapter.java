package com.adg.shared.adapter.persistence;

import com.adg.shared.dto.CampaignItem;
import com.adg.shared.port.CampaignPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CampaignPort 구현체. campaign_placements 테이블 조회. 1분 캐시 적용.
 * app.decision.use-http-dependencies=false(기본)일 때 활성화.
 */
@Component
@ConditionalOnProperty(name = "app.decision.use-http-dependencies", havingValue = "false", matchIfMissing = true)
public class CampaignPersistenceAdapter implements CampaignPort {

    private final CampaignPlacementRepository repository;

    public CampaignPersistenceAdapter(CampaignPlacementRepository repository) {
        this.repository = repository;
    }

    @Override
    @Cacheable(cacheNames = "campaigns", cacheManager = "decisionCacheManager")
    public List<CampaignItem> getCampaigns(String placementId) {
        List<CampaignPlacementEntity> list = repository.findByPlacementId(placementId);
        if (list == null || list.isEmpty()) return Collections.emptyList();
        return list.stream().map(this::toItem).collect(Collectors.toList());
    }

    private CampaignItem toItem(CampaignPlacementEntity e) {
        return CampaignItem.builder()
                .campaignId(e.getCampaignId())
                .creativeId(e.getCreativeId())
                .status(e.getStatus())
                .budget(e.getBudget())
                .targetConditions(e.getTargetConditions())
                .bid(e.getBid())
                .build();
    }
}
