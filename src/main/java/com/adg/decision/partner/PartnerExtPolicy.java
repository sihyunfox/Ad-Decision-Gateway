package com.adg.decision.partner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 파트너별 ext 정책: 요청 검증 규칙, 응답 ext 주입 내용.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerExtPolicy {

    /** 요청 request.ext / imp[].ext 에서 필수로 있어야 할 키 (null이면 검증 생략) */
    @Builder.Default
    private List<String> requestRequiredKeys = Collections.emptyList();
    /** 요청에서 허용하는 키 화이트리스트 (null이면 pass-through, 비어 있으면 ext 허용 안 함) */
    private List<String> requestAllowedKeys;
    /** 응답에 주입할 ext (BidResponse.ext, Bid.ext 등). null이면 주입 안 함. */
    private Map<String, Object> responseExt;

    public List<String> getRequestRequiredKeys() {
        return requestRequiredKeys == null ? Collections.emptyList() : requestRequiredKeys;
    }
}
