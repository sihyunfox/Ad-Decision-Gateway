package com.adg;

import com.adg.config.PartnerExtPolicyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Ad Decision Gateway (ADG) 메인 애플리케이션 진입점.
 * <p>
 * 제공 기능:
 * <ul>
 *   <li>Decision API: 광고 의사결정 요청 수신, downstream 병렬 호출, 필터/스코어링, fallback</li>
 *   <li>Event API: 노출/클릭 이벤트 수집 (idempotency, 인메모리 큐 적재)</li>
 *   <li>Admin API: 정책 조회/수정, audit 로그(큐 적재)</li>
 *   <li>인메모리 이벤트 큐: 주기 drain으로 파일(events.ndjson) 기록, graceful shutdown 시 큐 비우기</li>
 * </ul>
 * {@link org.springframework.scheduling.annotation.EnableScheduling}으로 EventQueueDrainer 스케줄링 활성화.
 */
@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(PartnerExtPolicyProperties.class)
public class AdDecisionGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdDecisionGatewayApplication.class, args);
    }
}
