package com.adg.shared.adapter.client;

import com.adg.shared.dto.CampaignItem;
import com.adg.shared.port.CampaignPort;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * Campaign 서비스 HTTP 클라이언트 어댑터. Mock 또는 실제 campaigns 엔드포인트 호출.
 * <p>
 * app.mock.enabled=true(기본)일 때만 빈 등록. Resilience4j CircuitBreaker·Retry·Bulkhead 적용.
 */
@Component
@ConditionalOnProperty(name = "app.decision.use-http-dependencies", havingValue = "true")
public class CampaignClientAdapter implements CampaignPort {

    private static final String DEPENDENCY_NAME = "campaign";
    private static final Logger log = LoggerFactory.getLogger(CampaignClientAdapter.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public CampaignClientAdapter(
            RestTemplate restTemplate,
            @Value("${app.mock.base-url:http://localhost:8080}") String baseUrl
    ) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    }

    @Override
    @CircuitBreaker(name = "campaign", fallbackMethod = "getCampaignsFallback")
    @Retry(name = "campaign")
    @Bulkhead(name = "campaign")
    public List<CampaignItem> getCampaigns(String placementId) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "mock/campaigns")
                .queryParam("placementId", placementId)
                .toUriString();
        long start = System.currentTimeMillis();
        try {
            ResponseEntity<List<CampaignItem>> res = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
            long durationMs = System.currentTimeMillis() - start;
            log.debug("dependency: dependencyName={}, durationMs={}, status=success", DEPENDENCY_NAME, durationMs);
            return res.getBody() != null ? res.getBody() : Collections.emptyList();
        } catch (Exception e) {
            long durationMs = System.currentTimeMillis() - start;
            log.warn("dependency: dependencyName={}, durationMs={}, status=failure", DEPENDENCY_NAME, durationMs);
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("unused")
    public List<CampaignItem> getCampaignsFallback(String placementId, Exception e) {
        log.warn("dependency: dependencyName={}, status=fallback", DEPENDENCY_NAME);
        return Collections.emptyList();
    }
}
