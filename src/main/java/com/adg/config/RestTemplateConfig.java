package com.adg.config;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * HTTP 클라이언트 및 Decision 파이프라인용 스레드 풀 설정.
 * <p>
 * app.http.* / app.decision.executor.* (application.yml) 기준으로 5k RPS 튜닝.
 * RestTemplate은 동일 호스트 연결 재사용을 위해 Apache HttpClient 5 연결 풀 사용.
 */
@Configuration
public class RestTemplateConfig {

    @Value("${app.http.connect-timeout-ms:100}")
    private int connectTimeoutMs;

    @Value("${app.http.read-timeout-ms:300}")
    private int readTimeoutMs;

    @Value("${app.http.max-conn-total:500}")
    private int maxConnTotal;

    @Value("${app.http.max-conn-per-route:500}")
    private int maxConnPerRoute;

    @Value("${app.decision.executor.core-pool-size:64}")
    private int executorCorePoolSize;

    @Value("${app.decision.executor.max-pool-size:256}")
    private int executorMaxPoolSize;

    @Value("${app.decision.executor.queue-capacity:512}")
    private int executorQueueCapacity;

    /**
     * Downstream HTTP 호출용 RestTemplate. 연결 풀로 동일 호스트 재사용, 타임아웃으로 장기 블로킹 방지.
     */
    @Bean
    public RestTemplate restTemplate() {
        var connectionConfig = ConnectionConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(connectTimeoutMs))
                .setSocketTimeout(Timeout.ofMilliseconds(readTimeoutMs))
                .build();

        var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setDefaultConnectionConfig(connectionConfig)
                .setMaxConnTotal(maxConnTotal)
                .setMaxConnPerRoute(maxConnPerRoute)
                .build();

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(connectTimeoutMs))
                .setResponseTimeout(Timeout.ofMilliseconds(readTimeoutMs))
                .build();

        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();

        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    /**
     * Decision 파이프라인 병렬 호출용 Executor (Profile/Campaign/Policy/Cap).
     * 5k RPS × 4 downstream 대비 풀 크기·큐는 application.yml에서 조정.
     */
    @Bean(name = "decisionExecutor")
    public Executor decisionExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(executorCorePoolSize);
        executor.setMaxPoolSize(executorMaxPoolSize);
        executor.setQueueCapacity(executorQueueCapacity);
        executor.setThreadNamePrefix("decision-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
