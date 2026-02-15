package com.adg.shared.adapter.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * apps 테이블에 대응하는 JPA 엔티티. 매체 앱.
 */
@Entity
@Table(name = "apps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app_id", nullable = false, length = 64)
    private String appId;

    @Column(name = "publisher_id")
    private Long publisherId;

    @Column(name = "name", length = 256)
    private String name;

    @Column(name = "bundle", length = 256)
    private String bundle;

    @Column(name = "domain", length = 256)
    private String domain;

    @Column(name = "storeurl", length = 1024)
    private String storeurl;

    @Column(name = "ver", length = 64)
    private String ver;

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
