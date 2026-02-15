package com.adg.shared.adapter.persistence;

import lombok.*;

import java.io.Serializable;

/**
 * cap_allowance 복합 PK.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CapAllowanceId implements Serializable {

    private String clientId;
    private String userId;
    private String campaignId;
}
