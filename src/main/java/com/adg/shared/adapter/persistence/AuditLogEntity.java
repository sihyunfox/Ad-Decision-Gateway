package com.adg.shared.adapter.persistence;

import jakarta.persistence.*;
import lombok.*;

/**
 * audit_log 테이블에 대응하는 JPA 엔티티.
 */
@Entity
@Table(name = "audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_type", nullable = false, length = 64)
    private String entityType;

    @Column(name = "entity_id", nullable = false, length = 128)
    private String entityId;

    @Column(name = "action", nullable = false, length = 32)
    private String action;

    @Lob
    @Column(name = "old_value", columnDefinition = "CLOB")
    private String oldValue;

    @Lob
    @Column(name = "new_value", columnDefinition = "CLOB")
    private String newValue;

    @Column(name = "actor", length = 128)
    private String actor;
}
