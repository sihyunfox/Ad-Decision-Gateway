package com.adg.shared.dto.openrtb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OpenRTB 2.x Video 객체 (Imp 내). 스펙 확장 시 필드 추가.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Video {

    private Integer w;
    private Integer h;
    private Integer mindur;
    private Integer maxdur;
}
