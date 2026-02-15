package com.adg.shared.port;

import com.adg.shared.dto.CapCheckRequest;
import com.adg.shared.dto.CapCheckResponse;

/**
 * 노출/예산 상한(Cap) 검사용 애플리케이션 포트.
 * <p>
 * Adapter에서 Mock 또는 실제 Cap 서비스 HTTP 호출로 구현.
 * Decision 파이프라인에서 유저·캠페인·클라이언트 조합별 노출 허용 여부 확인에 사용.
 */
public interface CapPort {

    /**
     * 요청 조건(유저, 캠페인, 클라이언트)에 대한 Cap 검사를 수행한다.
     *
     * @param request Cap 검사 요청
     * @return 허용 여부 및 남은 수량 등
     */
    CapCheckResponse check(CapCheckRequest request);
}
