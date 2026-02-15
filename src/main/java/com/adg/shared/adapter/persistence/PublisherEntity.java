package com.adg.shared.adapter.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * publishers 테이블에 대응하는 JPA 엔티티. 매체 퍼블리셔.
 */
@Entity
@Table(name = "publishers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublisherEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "publisher_id", nullable = false, length = 64)
    private String publisherId;

    @Column(name = "name", length = 256)
    private String name;

    @Column(name = "domain", length = 256)
    private String domain;

    @Lob
    @Column(name = "ext_json", columnDefinition = "CLOB")
    private String extJson;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
