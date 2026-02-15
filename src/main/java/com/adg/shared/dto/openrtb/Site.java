package com.adg.shared.dto.openrtb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
