package com.adg.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

/**
 * 로컬 캐시 설정. Caffeine 기반으로 광고/소재·정책·앱·사이트·퍼블리셔 등 DB 조회 성능 향상.
 * Decision 의존(Profile/Campaign/Policy/Cap)은 decisionCacheManager로 1분 TTL 적용.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    @Primary
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager(
                "creatives", "policies", "apps", "sites", "publishers");
        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES));
        return manager;
    }

    /** Decision 파이프라인 DataLoadStage용 1분 TTL 캐시 (profiles, campaigns, policies, cap). */
    @Bean(name = "decisionCacheManager")
    public CacheManager decisionCacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager(
                "profiles", "campaigns", "policies", "cap");
        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(60, TimeUnit.SECONDS));
        return manager;
    }
}
