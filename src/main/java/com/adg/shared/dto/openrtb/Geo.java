package com.adg.shared.dto.openrtb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OpenRTB 2.x Geo 객체.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Geo {

    private String country;
    private String region;
    private String city;
    private Double lat;
    private Double lon;
}
