package com.adg.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * app.partner.policies 설정 바인딩. partnerId → 정책.
 */
@Data
@ConfigurationProperties(prefix = "app.partner")
public class PartnerExtPolicyProperties {

    /** partnerId → 정책 내용 */
    private Map<String, PolicyEntry> policies = Collections.emptyMap();

    @Data
    public static class PolicyEntry {
        private List<String> requestRequiredKeys = Collections.emptyList();
        private List<String> requestAllowedKeys;
        private Map<String, Object> responseExt;
    }
}
