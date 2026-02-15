package com.adg.shared.adapter.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * sites 테이블에 대응하는 JPA 엔티티. 매체 사이트.
 */
@Entity
@Table(name = "sites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "site_id", nullable = false, length = 64)
    private String siteId;

    @Column(name = "publisher_id")
    private Long publisherId;

    @Column(name = "name", length = 256)
    private String name;

    @Column(name = "domain", length = 256)
    private String domain;

    @Column(name = "page", length = 1024)
    private String page;

    @Column(name = "ref", length = 1024)
    private String ref;

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
