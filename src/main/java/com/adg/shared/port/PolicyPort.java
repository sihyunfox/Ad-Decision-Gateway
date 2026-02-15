package com.adg.shared.port;

import com.adg.shared.dto.PolicyResponse;

import java.util.List;

/**
 * 클라이언트별 정책(필터 규칙, AB 플래그, 타임아웃 설정 등) 조회용 애플리케이션 포트.
 * <p>
 * Adapter에서 Mock 또는 실제 Policy 서비스 HTTP 호출로 구현.
 * Decision 파이프라인에서 필터·스코어링 정책 적용에 사용.
 */
public interface PolicyPort {

    /**
     * clientId에 해당하는 정책 목록을 조회한다.
     *
     * @param clientId 클라이언트 ID
     * @return 정책 목록 (빈 목록 가능)
     */
    List<PolicyResponse> getPolicies(String clientId);
}
