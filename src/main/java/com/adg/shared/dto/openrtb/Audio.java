package com.adg.shared.dto.openrtb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OpenRTB 2.x Audio 객체 (Imp 내). 스펙 확장 시 필드 추가.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Audio {

    private Integer seq;
    private Integer mindur;
    private Integer maxdur;
}
