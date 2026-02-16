package com.adg.decision.partner;

import com.adg.config.PartnerExtPolicyProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * application.yml(app.partner.policies) 기반 파트너 ext 정책 조회.
 */
@Component
public class ConfigurablePartnerExtPolicyRegistry implements PartnerExtPolicyRegistry {

    private final PartnerExtPolicyProperties properties;

    public ConfigurablePartnerExtPolicyRegistry(PartnerExtPolicyProperties properties) {
        this.properties = properties != null ? properties : new PartnerExtPolicyProperties();
    }

    @Override
    public PartnerExtPolicy getPolicy(String partnerId) {
        if (partnerId == null || properties.getPolicies() == null) {
            return defaultPolicy();
        }
        PartnerExtPolicyProperties.PolicyEntry entry = properties.getPolicies().get(partnerId);
        if (entry == null) {
            return defaultPolicy();
        }
        return PartnerExtPolicy.builder()
                .requestRequiredKeys(entry.getRequestRequiredKeys() != null ? entry.getRequestRequiredKeys() : Collections.emptyList())
                .requestAllowedKeys(entry.getRequestAllowedKeys())
                .responseExt(entry.getResponseExt() != null ? entry.getResponseExt() : Collections.emptyMap())
                .build();
    }

    private static PartnerExtPolicy defaultPolicy() {
        return PartnerExtPolicy.builder()
                .requestRequiredKeys(Collections.emptyList())
                .requestAllowedKeys(null)
                .responseExt(Collections.emptyMap())
                .build();
    }
}
