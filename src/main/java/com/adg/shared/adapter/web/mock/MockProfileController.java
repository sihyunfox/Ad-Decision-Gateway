package com.adg.shared.adapter.web.mock;

import com.adg.shared.dto.ProfileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 로컬/개발용 Mock Profile 서비스.
 */
@RestController
@RequestMapping("/mock/profile")
public class MockProfileController {

    @GetMapping("/{userId}")
    public ResponseEntity<ProfileResponse> getProfile(
            @PathVariable String userId,
            @RequestParam(required = false) Integer delayMs,
            @RequestParam(required = false) Double failRate,
            @RequestParam(required = false) Boolean forceTimeout
    ) {
        MockBehavior.simulate(delayMs, failRate, forceTimeout);

        ProfileResponse body = ProfileResponse.builder()
                .userId(userId)
                .segment(List.of("premium", "mobile"))
                .interests(List.of("sports", "tech"))
                .riskScore(0.1)
                .build();
        return ResponseEntity.ok(body);
    }
}
