package com.adg.shared.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * event_url_log 테이블 접근.
 */
public interface EventUrlLogRepository extends JpaRepository<EventUrlLogEntity, Long> {
}
