package com.adg.shared.adapter.persistence;

import jakarta.persistence.*;
import lombok.*;

/**
 * event_url_log 테이블에 대응하는 JPA 엔티티.
 */
@Entity
@Table(name = "event_url_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventUrlLogEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_type", nullable = false, length = 32)
    private String eventType;

    @Column(name = "url", nullable = false, length = 2048)
    private String url;
}
