package com.adg.shared.dto.openrtb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OpenRTB 2.x Imp(ression) 객체.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Imp {

    /** 필수. 노출 단위 ID */
    private String id;
    private Banner banner;
    // video, native 등 확장 시 추가
}
