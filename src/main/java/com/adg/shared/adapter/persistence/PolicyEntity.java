package com.adg.shared.adapter.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * policies 테이블에 대응하는 JPA 엔티티.
 */
@Entity
@Table(name = "policies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", nullable = false, length = 64)
    private String clientId;

    @Lob
    @Column(name = "filter_rules", columnDefinition = "CLOB")
    private String filterRules;

    @Lob
    @Column(name = "ab_flags", columnDefinition = "CLOB")
    private String abFlags;

    @Lob
    @Column(name = "timeout_config", columnDefinition = "CLOB")
    private String timeoutConfig;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
