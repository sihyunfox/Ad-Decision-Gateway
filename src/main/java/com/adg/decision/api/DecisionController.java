package com.adg.decision.api;

import com.adg.decision.app.DecisionService;
import com.adg.shared.adapter.web.AuthFilter;
import com.adg.shared.dto.openrtb.BidRequest;
import com.adg.shared.dto.openrtb.BidResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Decision API HTTP 컨트롤러.
 * <p>POST /v1/decision — OpenRTB 2.x Bid Request 수신, Bid Response 반환. 인증 없음(public path).</p>
 */
@RestController
@RequestMapping("/v1")
@Tag(name = "Decision", description = "Ad decision API (OpenRTB 2.x)")
public class DecisionController {

    private final DecisionService decisionService;

    @Value("${app.decision.fallback-return-503:false}")
    private boolean fallbackReturn503;

    public DecisionController(DecisionService decisionService) {
        this.decisionService = decisionService;
    }

    /** OpenRTB Bid Request를 받아 의사결정 후 Bid Response 반환. clientId는 필터에서 설정된 값(또는 openrtb) 사용. */
    @PostMapping("/decision")
    @Operation(summary = "Request ad decision (OpenRTB Bid Request)")
    public ResponseEntity<BidResponse> decide(
            @Valid @RequestBody BidRequest request,
            HttpServletRequest httpRequest
    ) {
        String clientId = (String) httpRequest.getAttribute(AuthFilter.ATTR_CLIENT_ID);
        if (clientId == null) {
            clientId = "openrtb";
        }
        BidResponse response = decisionService.decide(request, clientId);
        boolean fallbackUsed = isFallbackResponse(response);
        if (response.getSeatbid() != null && !response.getSeatbid().isEmpty()
                && response.getSeatbid().get(0).getBid() != null && !response.getSeatbid().get(0).getBid().isEmpty()) {
            httpRequest.setAttribute("decisionId", response.getSeatbid().get(0).getBid().get(0).getId());
        }
        httpRequest.setAttribute("fallbackUsed", fallbackUsed);
        if (fallbackReturn503 && fallbackUsed) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
        return ResponseEntity.ok(response);
    }

    /** seatbid가 없거나, 첫 bid가 house-default(crid) 또는 house(adid)이면 fallback으로 간주. */
    private boolean isFallbackResponse(BidResponse r) {
        if (r.getSeatbid() == null || r.getSeatbid().isEmpty()) return true;
        var bids = r.getSeatbid().get(0).getBid();
        if (bids == null || bids.isEmpty()) return true;
        var bid = bids.get(0);
        return "house-default".equals(bid.getCrid()) || (bid.getCrid() == null && "house".equals(bid.getAdid()));
    }
}
