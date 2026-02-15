package com.adg.shared.port;

import com.adg.shared.dto.DecisionRecord;

/**
 * Decision 처리 결과를 decision_history에 저장하기 위한 애플리케이션 포트.
 * <p>
 * Adapter에서 JPA Repository를 통해 persist. 이벤트 연계·감사·분석용 이력 보관.
 */
public interface DecisionHistoryPort {

    /**
     * 의사결정 결과 레코드를 저장한다.
     *
     * @param record 저장할 Decision 레코드 (decisionId, winner, fallbackUsed 등)
     */
    void save(DecisionRecord record);
}
