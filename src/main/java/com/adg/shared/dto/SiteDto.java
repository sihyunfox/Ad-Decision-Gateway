package com.adg.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사이트(Site) 요청·응답 DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteDto {

    private Long id;
    private String siteId;
    private Long publisherId;
    private String name;
    private String domain;
    private String page;
    private String ref;
    private String extJson;
}
