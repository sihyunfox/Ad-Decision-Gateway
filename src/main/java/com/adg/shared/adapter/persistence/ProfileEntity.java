package com.adg.shared.adapter.persistence;

import jakarta.persistence.*;
import lombok.*;

/**
 * profiles 테이블에 대응하는 JPA 엔티티. 유저 프로필(세그먼트, 관심사, 리스크).
 */
@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileEntity extends BaseEntity {

    @Id
    @Column(name = "user_id", length = 64)
    private String userId;

    @Lob
    @Column(name = "segment", columnDefinition = "CLOB")
    private String segment;

    @Lob
    @Column(name = "interests", columnDefinition = "CLOB")
    private String interests;

    @Column(name = "risk_score")
    private Double riskScore;
}
