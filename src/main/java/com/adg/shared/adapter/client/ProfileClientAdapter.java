package com.adg.shared.adapter.client;

import com.adg.shared.dto.ProfileResponse;
import com.adg.shared.port.ProfilePort;
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

import java.util.Optional;

/**
 * Profile 서비스 HTTP 클라이언트 어댑터. Mock 또는 실제 profile 엔드포인트 호출.
 * <p>
 * app.mock.enabled=true(기본)일 때만 빈 등록. Resilience4j CircuitBreaker·Retry·Bulkhead 적용.
 */
@Component
@ConditionalOnProperty(name = "app.decision.use-http-dependencies", havingValue = "true")
public class ProfileClientAdapter implements ProfilePort {

    private static final String DEPENDENCY_NAME = "profile";
    private static final Logger log = LoggerFactory.getLogger(ProfileClientAdapter.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ProfileClientAdapter(
            RestTemplate restTemplate,
            @Value("${app.mock.base-url:http://localhost:8080}") String baseUrl
    ) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    }

    @Override
    @CircuitBreaker(name = "profile", fallbackMethod = "getProfileFallback")
    @Retry(name = "profile")
    @Bulkhead(name = "profile")
    public Optional<ProfileResponse> getProfile(String userId) {
        String url = baseUrl + "mock/profile/" + userId;
        long start = System.currentTimeMillis();
        try {
            ResponseEntity<ProfileResponse> res = restTemplate.getForEntity(url, ProfileResponse.class);
            long durationMs = System.currentTimeMillis() - start;
            log.debug("dependency: dependencyName={}, durationMs={}, status=success", DEPENDENCY_NAME, durationMs);
            return Optional.ofNullable(res.getBody());
        } catch (Exception e) {
            long durationMs = System.currentTimeMillis() - start;
            log.warn("dependency: dependencyName={}, durationMs={}, status=failure", DEPENDENCY_NAME, durationMs);
            return Optional.empty();
        }
    }

    @SuppressWarnings("unused")
    public Optional<ProfileResponse> getProfileFallback(String userId, Exception e) {
        log.warn("dependency: dependencyName={}, status=fallback", DEPENDENCY_NAME);
        return Optional.empty();
    }
}
