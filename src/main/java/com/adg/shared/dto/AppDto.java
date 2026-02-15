package com.adg.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 앱(App) 요청·응답 DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppDto {

    private Long id;
    private String appId;
    private Long publisherId;
    private String name;
    private String bundle;
    private String domain;
    private String storeurl;
    private String ver;
    private String extJson;
}
