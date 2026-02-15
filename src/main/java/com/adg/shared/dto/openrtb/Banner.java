package com.adg.shared.dto.openrtb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OpenRTB 2.x Banner 객체 (Imp 내).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Banner {

    private Integer w;
    private Integer h;
    private Integer wmax;
    private Integer hmax;
}
