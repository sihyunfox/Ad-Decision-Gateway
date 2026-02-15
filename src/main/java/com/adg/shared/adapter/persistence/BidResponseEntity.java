package com.adg.shared.adapter.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * bid_responses 테이블에 대응하는 JPA 엔티티.
 */
@Entity
@Table(name = "bid_responses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BidResponseEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", nullable = false, length = 64)
    private String requestId;

    @Column(name = "bid_response_id", length = 64)
    private String bidResponseId;

    @Column(name = "seat", length = 64)
    private String seat;

    @Column(name = "bid_impid", length = 64)
    private String bidImpid;

    @Column(name = "bid_price", precision = 19, scale = 4)
    private BigDecimal bidPrice;

    @Column(name = "bid_crid", length = 64)
    private String bidCrid;

    @Column(name = "bid_adid", length = 64)
    private String bidAdid;

    @Lob
    @Column(name = "raw_json", columnDefinition = "CLOB")
    private String rawJson;

    @Column(name = "responded_at", nullable = false)
    private Instant respondedAt;
}
