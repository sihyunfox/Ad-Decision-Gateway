package com.adg.shared.adapter.web.mock;

import com.adg.shared.dto.CapCheckRequest;
import com.adg.shared.dto.CapCheckResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 로컬/개발용 Mock Cap 서비스.
 */
@RestController
@RequestMapping("/mock/cap")
public class MockCapController {

    @PostMapping("/check")
    public ResponseEntity<CapCheckResponse> check(
            @RequestBody CapCheckRequest request,
            @RequestParam(required = false) Integer delayMs,
            @RequestParam(required = false) Double failRate,
            @RequestParam(required = false) Boolean forceTimeout
    ) {
        MockBehavior.simulate(delayMs, failRate, forceTimeout);

        CapCheckResponse body = CapCheckResponse.builder()
                .allow(true)
                .remaining(100)
                .build();
        return ResponseEntity.ok(body);
    }
}
