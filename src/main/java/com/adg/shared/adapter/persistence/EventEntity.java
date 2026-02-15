package com.adg.shared.adapter.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * events 테이블에 대응하는 JPA 엔티티.
 */
@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventEntity extends BaseEntity {

    @Id
    @Column(name = "event_id", length = 128)
    private String eventId;

    @Column(name = "decision_id", length = 64)
    private String decisionId;

    @Column(name = "client_id", nullable = false, length = 64)
    private String clientId;

    @Column(name = "event_type", nullable = false, length = 32)
    private String eventType;

    @Lob
    @Column(name = "payload_json", columnDefinition = "CLOB")
    private String payloadJson;

    @Column(name = "event_timestamp")
    private Instant eventTimestamp;

    @Column(name = "crid", length = 64)
    private String crid;

    @Column(name = "impid", length = 64)
    private String impid;

    @Column(name = "site_id", length = 64)
    private String siteId;

    @Column(name = "app_id", length = 64)
    private String appId;
}
