package com.adg.shared.dto.openrtb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OpenRTB 2.x Publisher 객체 (Site/App 내).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Publisher {

    private String id;
    private String name;
    private String domain;
}
