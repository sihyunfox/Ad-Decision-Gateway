package com.adg.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * app.auth 설정 바인딩. API Key 기반 인증.
 * dev-mode=true 이면 API 키 검증을 수행하지 않고 기본 clientId로 통과시킴.
 */
@Component
@ConfigurationProperties(prefix = "app.auth")
public class AppAuthProperties {

    /** true: API 키 검증 생략, 모든 요청을 default client로 통과 (로컬/개발용) */
    private boolean devMode = false;
    private Map<String, String> apiKeys = Collections.emptyMap();
    private String adminKey = "";
    private List<String> publicPaths = Collections.emptyList();

    public boolean isDevMode() {
        return devMode;
    }

    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }

    public Map<String, String> getApiKeys() {
        return apiKeys;
    }

    public void setApiKeys(Map<String, String> apiKeys) {
        this.apiKeys = apiKeys != null ? apiKeys : Collections.emptyMap();
    }

    public String getAdminKey() {
        return adminKey;
    }

    public void setAdminKey(String adminKey) {
        this.adminKey = adminKey != null ? adminKey : "";
    }

    public List<String> getPublicPaths() {
        return publicPaths;
    }

    public void setPublicPaths(List<String> publicPaths) {
        this.publicPaths = publicPaths != null ? publicPaths : Collections.emptyList();
    }
}
