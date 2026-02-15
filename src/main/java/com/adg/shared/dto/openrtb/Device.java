package com.adg.shared.dto.openrtb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OpenRTB 2.x Device 객체.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    private String ua;
    private Geo geo;
    private String ip;
    private Integer devicetype;
    private String os;
    private String model;
    private String ifa;  // IDFA
    private String gaid; // GAID
}
