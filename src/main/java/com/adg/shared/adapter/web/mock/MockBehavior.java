package com.adg.shared.adapter.web.mock;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Mock 컨트롤러 공통 동작: 지연(delayMs), 실패 확률(failRate), 강제 타임아웃(forceTimeout).
 */
final class MockBehavior {

    private MockBehavior() {
    }

    static void simulate(Integer delayMs, Double failRate, Boolean forceTimeout) {
        if (Boolean.TRUE.equals(forceTimeout)) {
            try {
                TimeUnit.SECONDS.sleep(60);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "timeout");
            }
        }
        if (delayMs != null && delayMs > 0) {
            try {
                TimeUnit.MILLISECONDS.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "interrupted");
            }
        }
        if (failRate != null && failRate > 0 && ThreadLocalRandom.current().nextDouble() < failRate) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "mock fail");
        }
    }
}
