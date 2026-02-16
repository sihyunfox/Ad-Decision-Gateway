package com.adg.decision.partner;

import com.adg.shared.dto.openrtb.BidRequest;
import com.adg.shared.dto.openrtb.Imp;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 파트너별 요청 ext 검증. 정책의 requestRequiredKeys / requestAllowedKeys 적용.
 * 실패 시 IllegalArgumentException (400).
 */
@Component
public class PartnerExtRequestValidator {

    private final PartnerExtPolicyRegistry registry;

    public PartnerExtRequestValidator(PartnerExtPolicyRegistry registry) {
        this.registry = registry;
    }

    /**
     * BidRequest의 ext, imp[].ext를 파트너 정책으로 검증.
     */
    public void validate(BidRequest request, String partnerId) {
        if (request == null) return;
        PartnerExtPolicy policy = registry.getPolicy(partnerId);

        List<String> required = policy.getRequestRequiredKeys();
        List<String> allowed = policy.getRequestAllowedKeys();

        if (required.isEmpty() && allowed == null) {
            return;
        }

        if (request.getExt() != null && !request.getExt().isEmpty()) {
            validateExt(request.getExt(), "request.ext", required, allowed);
        } else if (!required.isEmpty()) {
            for (String key : required) {
                throw new IllegalArgumentException("request.ext: required key '" + key + "' is missing");
            }
        }

        if (request.getImp() != null) {
            int i = 0;
            for (Imp imp : request.getImp()) {
                if (imp.getExt() != null && !imp.getExt().isEmpty()) {
                    validateExt(imp.getExt(), "imp[" + i + "].ext", required, allowed);
                } else if (!required.isEmpty()) {
                    for (String key : required) {
                        throw new IllegalArgumentException("imp[" + i + "].ext: required key '" + key + "' is missing");
                    }
                }
                i++;
            }
        }
    }

    private void validateExt(Map<String, Object> ext, String location, List<String> required, List<String> allowed) {
        for (String key : required) {
            if (!ext.containsKey(key)) {
                throw new IllegalArgumentException(location + ": required key '" + key + "' is missing");
            }
        }
        if (allowed != null) {
            for (String key : ext.keySet()) {
                if (!allowed.contains(key)) {
                    throw new IllegalArgumentException(location + ": key '" + key + "' is not allowed for this partner");
                }
            }
        }
    }
}
