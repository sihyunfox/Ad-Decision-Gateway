package com.adg.shared.adapter.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * bid_requests 테이블에 대응하는 JPA 엔티티.
 */
@Entity
@Table(name = "bid_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BidRequestEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", nullable = false, length = 64)
    private String requestId;

    @Lob
    @Column(name = "raw_json", columnDefinition = "CLOB")
    private String rawJson;

    @Column(name = "imp_ids", length = 512)
    private String impIds;

    @Column(name = "site_id", length = 64)
    private String siteId;

    @Column(name = "app_id", length = 64)
    private String appId;

    @Column(name = "publisher_id", length = 64)
    private String publisherId;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;
}
