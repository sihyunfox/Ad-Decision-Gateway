package com.adg.decision.partner;

/**
 * partnerId → PartnerExtPolicy 조회.
 */
public interface PartnerExtPolicyRegistry {

    /**
     * 파트너 정책 조회. 없으면 기본 정책(pass-through, 응답 ext 없음) 반환.
     */
    PartnerExtPolicy getPolicy(String partnerId);
}
