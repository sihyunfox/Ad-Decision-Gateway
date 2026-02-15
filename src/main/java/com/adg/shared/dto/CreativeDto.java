package com.adg.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 광고 소재(Creative) 요청·응답 DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreativeDto {

    private Long id;
    private String creativeId;
    private String campaignId;
    private String name;
    private String format;
    private Integer width;
    private Integer height;
    private String mimeType;
    private String admSnippet;
    private String landingUrl;
    private String status;
}
