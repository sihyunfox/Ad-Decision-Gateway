package com.adg.shared.adapter.persistence;

import com.adg.shared.dto.CapCheckRequest;
import com.adg.shared.dto.CapCheckResponse;
import com.adg.shared.port.CapPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CapPort 구현체. cap_allowance 테이블 조회. 1분 캐시 적용.
 * app.decision.use-http-dependencies=false(기본)일 때 활성화.
 */
@Component
@ConditionalOnProperty(name = "app.decision.use-http-dependencies", havingValue = "false", matchIfMissing = true)
public class CapPersistenceAdapter implements CapPort {

    private static final String WILDCARD_CAMPAIGN = "*";

    private final CapAllowanceRepository repository;

    public CapPersistenceAdapter(CapAllowanceRepository repository) {
        this.repository = repository;
    }

    @Override
    @Cacheable(cacheNames = "cap", cacheManager = "decisionCacheManager", key = "#request.clientId + '-' + #request.userId + '-' + (T(java.util.Objects).requireNonNullElse(#request.campaignId, '*'))")
    public CapCheckResponse check(CapCheckRequest request) {
        String clientId = request.getClientId() != null ? request.getClientId() : "";
        String userId = request.getUserId() != null ? request.getUserId() : "unknown";
        String campaignId = request.getCampaignId() != null && !request.getCampaignId().isBlank()
                ? request.getCampaignId() : WILDCARD_CAMPAIGN;

        List<CapAllowanceEntity> list = repository.findByClientIdAndUserId(clientId, userId);
        for (CapAllowanceEntity e : list) {
            if (e.getCampaignId().equals(campaignId) || e.getCampaignId().equals(WILDCARD_CAMPAIGN)) {
                int remaining = e.getRemaining();
                return CapCheckResponse.builder()
                        .allow(remaining > 0)
                        .remaining(remaining)
                        .build();
            }
        }
        return CapCheckResponse.builder().allow(false).remaining(0).build();
    }
}
