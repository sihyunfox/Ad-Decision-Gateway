package com.adg.shared.dto.openrtb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * OpenRTB 2.x Site 객체.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Site {

    private String id;
    private String name;
    private String domain;
    private String page;
    private String ref;
    private Publisher publisher;
    /** OpenRTB 2.x 확장 (파트너별 커스텀) */
    private Map<String, Object> ext;
}
