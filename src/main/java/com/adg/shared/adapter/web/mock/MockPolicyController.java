package com.adg.shared.adapter.web.mock;

import com.adg.shared.dto.PolicyResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 로컬/개발용 Mock Policy 서비스.
 */
@RestController
@RequestMapping("/mock/policies")
public class MockPolicyController {

    @GetMapping
    public ResponseEntity<List<PolicyResponse>> getPolicies(
            @RequestParam String clientId,
            @RequestParam(required = false) Integer delayMs,
            @RequestParam(required = false) Double failRate,
            @RequestParam(required = false) Boolean forceTimeout
    ) {
        MockBehavior.simulate(delayMs, failRate, forceTimeout);

        List<PolicyResponse> body = List.of(
                PolicyResponse.builder()
                        .clientId(clientId)
                        .filterRules("{}")
                        .abFlags("{}")
                        .timeoutConfig("{\"default\":30}")
                        .build()
        );
        return ResponseEntity.ok(body);
    }
}
