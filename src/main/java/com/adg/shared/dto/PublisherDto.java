package com.adg.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 퍼블리셔(Publisher) 요청·응답 DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublisherDto {

    private Long id;
    private String publisherId;
    private String name;
    private String domain;
    private String extJson;
}
