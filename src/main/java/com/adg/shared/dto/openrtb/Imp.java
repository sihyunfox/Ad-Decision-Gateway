package com.adg.shared.dto.openrtb;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * OpenRTB 2.x Imp(ression) 객체.
 * <p>필수: id. Banner/Video/Audio/Native 중 최소 1개(OpenRTB 2.6).</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Imp {

    /** 필수. 노출 단위 ID */
    @NotBlank(message = "imp.id is required")
    private String id;
    private Banner banner;
    private Video video;
    private Audio audio;
    /** OpenRTB 필드명 "native" (Java 예약어 회피) */
    @JsonProperty("native")
    private Native nativeAd;
    /** OpenRTB 2.x 확장 (파트너별 커스텀) */
    private Map<String, Object> ext;
}
