package com.adg.shared.adapter.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * creatives 테이블에 대응하는 JPA 엔티티. 광고 소재(Creative) 정보.
 */
@Entity
@Table(name = "creatives")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreativeEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creative_id", nullable = false, length = 64)
    private String creativeId;

    @Column(name = "campaign_id", nullable = false, length = 64)
    private String campaignId;

    @Column(name = "name", length = 256)
    private String name;

    @Column(name = "format", nullable = false, length = 32)
    private String format;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "mime_type", length = 128)
    private String mimeType;

    @Lob
    @Column(name = "adm_snippet", columnDefinition = "CLOB")
    private String admSnippet;

    @Column(name = "landing_url", length = 1024)
    private String landingUrl;

    @Column(name = "status", nullable = false, length = 16)
    private String status;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
