package com.adg.shared.adapter.client;

import com.adg.shared.dto.CapCheckRequest;
import com.adg.shared.dto.CapCheckResponse;
import com.adg.shared.port.CapPort;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cap 서비스 HTTP 클라이언트 어댑터. Mock 또는 실제 cap/check 엔드포인트 호출.
 * <p>
 * app.mock.enabled=true(기본)일 때만 빈 등록. Resilience4j CircuitBreaker·Retry·Bulkhead 적용.
 */
@Component
@ConditionalOnProperty(name = "app.decision.use-http-dependencies", havingValue = "true")
public class CapClientAdapter implements CapPort {

    private static final String DEPENDENCY_NAME = "cap";
    private static final Logger log = LoggerFactory.getLogger(CapClientAdapter.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public CapClientAdapter(
            RestTemplate restTemplate,
            @Value("${app.mock.base-url:http://localhost:8080}") String baseUrl
    ) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    }

    @Override
    @CircuitBreaker(name = "cap", fallbackMethod = "checkFallback")
    @Retry(name = "cap")
    @Bulkhead(name = "cap")
    public CapCheckResponse check(CapCheckRequest request) {
        String url = baseUrl + "mock/cap/check";
        long start = System.currentTimeMillis();
        try {
            ResponseEntity<CapCheckResponse> res = restTemplate.postForEntity(url, request, CapCheckResponse.class);
            long durationMs = System.currentTimeMillis() - start;
            log.debug("dependency: dependencyName={}, durationMs={}, status=success", DEPENDENCY_NAME, durationMs);
            return res.getBody() != null ? res.getBody() : CapCheckResponse.builder().allow(false).remaining(0).build();
        } catch (Exception e) {
            long durationMs = System.currentTimeMillis() - start;
            log.warn("dependency: dependencyName={}, durationMs={}, status=failure", DEPENDENCY_NAME, durationMs);
            return CapCheckResponse.builder().allow(false).remaining(0).build();
        }
    }

    @SuppressWarnings("unused")
    public CapCheckResponse checkFallback(CapCheckRequest request, Exception e) {
        log.warn("dependency: dependencyName={}, status=fallback", DEPENDENCY_NAME);
        return CapCheckResponse.builder().allow(false).remaining(0).build();
    }
}
