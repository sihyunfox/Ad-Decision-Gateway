package com.adg.shared.adapter.persistence;

import jakarta.persistence.*;
import lombok.*;

/**
 * cap_allowance 테이블에 대응하는 JPA 엔티티. Cap 검사(남은 허용량).
 */
@Entity
@Table(name = "cap_allowance")
@IdClass(CapAllowanceId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapAllowanceEntity extends BaseEntity {

    @Id
    @Column(name = "client_id", nullable = false, length = 64)
    private String clientId;

    @Id
    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    @Id
    @Column(name = "campaign_id", nullable = false, length = 64)
    private String campaignId;

    @Column(name = "remaining", nullable = false)
    private int remaining;
}
