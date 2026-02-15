package com.adg.shared.port;

import com.adg.shared.dto.CampaignItem;

import java.util.List;

/**
 * 배치(placement)별 후보 캠페인 목록 조회용 애플리케이션 포트.
 * <p>
 * Adapter에서 Mock 또는 실제 Campaign 서비스 HTTP 호출로 구현.
 * Decision 파이프라인에서 필터·우승자 선정의 입력으로 사용.
 */
public interface CampaignPort {

    /**
     * placementId에 해당하는 후보 캠페인 목록을 조회한다.
     *
     * @param placementId 광고 배치 ID
     * @return 캠페인 목록 (빈 목록 가능)
     */
    List<CampaignItem> getCampaigns(String placementId);
}
