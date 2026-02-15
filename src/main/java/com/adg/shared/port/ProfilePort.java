package com.adg.shared.port;

import com.adg.shared.dto.ProfileResponse;

import java.util.Optional;

/**
 * 유저 프로필(세그먼트, 관심사, 리스크 스코어) 조회용 애플리케이션 포트.
 * <p>
 * Adapter에서 Mock 또는 실제 Profile 서비스 HTTP 호출로 구현.
 * Decision 파이프라인에서 타겟팅·필터·스코어링에 사용.
 */
public interface ProfilePort {

    /**
     * userId에 해당하는 프로필을 조회한다.
     *
     * @param userId 사용자 식별자
     * @return 프로필 (없으면 empty)
     */
    Optional<ProfileResponse> getProfile(String userId);
}
