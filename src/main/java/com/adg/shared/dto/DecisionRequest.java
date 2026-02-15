package com.adg.shared.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Decision API 요청 바디 DTO.
 * <p>
 * 클라이언트 ID·배치 ID는 필수이며, 디바이스/유저/지역/광고 크기 등 컨텍스트는 선택.
 * 로깅·다운스트림 전달 시 민감 필드(device.idfa, user.userId 등)는 마스킹 대상.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecisionRequest {

    /** 클라이언트가 부여한 요청 ID (선택, 로깅·추적용). */
    private String requestId;

    @NotBlank(message = "clientId is required")
    private String clientId;

    @NotBlank(message = "placementId is required")
    private String placementId;

    private String appId;
    private String siteId;

    @Valid
    private DeviceInfo device;

    @Valid
    private UserInfo user;

    @Valid
    private GeoInfo geo;

    @Valid
    private AdSize adSize;

    /** true 시 디버그 정보를 응답에 포함. */
    private Boolean debug;

    /** 디바이스 정보 (OS, 모델, 광고 식별자 등). idfa/aaid는 민감 정보. */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeviceInfo {
        private String os;
        private String model;
        private String idfa;
        private String aaid;
    }

    /** 사용자/세션 정보. userId는 민감 정보. */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String userId;
        private String sessionId;
    }

    /** 지역 정보. */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeoInfo {
        private String country;
        private String region;
    }

    /** 광고 슬롯 크기 (폭·높이). */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdSize {
        private Integer w;
        private Integer h;
    }
}
