package com.adg.shared.dto.openrtb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * OpenRTB 2.x App 객체.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class App {

    private String id;
    private String name;
    private String bundle;
    private String domain;
    private String ver;
    private Publisher publisher;
    /** OpenRTB 2.x 확장 (파트너별 커스텀) */
    private Map<String, Object> ext;
}
