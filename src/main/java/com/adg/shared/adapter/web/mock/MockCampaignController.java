package com.adg.shared.adapter.web.mock;

import com.adg.shared.dto.CampaignItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 로컬/개발용 Mock Campaign 서비스.
 */
@RestController
@RequestMapping("/mock/campaigns")
public class MockCampaignController {

    @GetMapping
    public ResponseEntity<List<CampaignItem>> getCampaigns(
            @RequestParam String placementId,
            @RequestParam(required = false) Integer delayMs,
            @RequestParam(required = false) Double failRate,
            @RequestParam(required = false) Boolean forceTimeout
    ) {
        MockBehavior.simulate(delayMs, failRate, forceTimeout);

        List<CampaignItem> body = List.of(
                CampaignItem.builder()
                        .campaignId("camp-1")
                        .creativeId("creative-1")
                        .status("ACTIVE")
                        .budget(new BigDecimal("10000"))
                        .targetConditions("{}")
                        .bid(new BigDecimal("1.50"))
                        .build(),
                CampaignItem.builder()
                        .campaignId("camp-2")
                        .creativeId("creative-2")
                        .status("ACTIVE")
                        .budget(new BigDecimal("5000"))
                        .targetConditions("{}")
                        .bid(new BigDecimal("1.20"))
                        .build()
        );
        return ResponseEntity.ok(body);
    }
}
