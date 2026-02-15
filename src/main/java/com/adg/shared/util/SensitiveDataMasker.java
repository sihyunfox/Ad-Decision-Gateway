package com.adg.shared.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Event meta·Request body 등에서 민감 필드(userId, deviceId, idfa, aaid 등)를 마스킹하는 유틸리티.
 * <p>
 * 저장(events.payload_json) 및 로그 출력 시 개인 식별자 노출을 막기 위해 사용.
 * 키 이름은 대소문자 구분 없이 매칭.
 */
public final class SensitiveDataMasker {

    private static final String MASK = "***";
    private static final Set<String> SENSITIVE_KEYS = Set.of(
            "userId", "user_id", "deviceId", "device_id", "idfa", "aaid", "sessionId", "session_id"
    );

    private SensitiveDataMasker() {
    }

    /**
     * meta 맵의 복사본을 만들고, 민감 키에 해당하는 값만 마스킹 문자열로 치환하여 반환한다.
     *
     * @param meta 원본 메타 맵 (null/empty 가능)
     * @return 마스킹된 새 맵 (원본이 null/empty면 그대로 반환)
     */
    public static Map<String, Object> maskMeta(Map<String, Object> meta) {
        if (meta == null || meta.isEmpty()) {
            return meta;
        }
        Map<String, Object> out = new HashMap<>();
        for (Map.Entry<String, Object> e : meta.entrySet()) {
            String key = e.getKey();
            boolean sensitive = SENSITIVE_KEYS.stream()
                    .anyMatch(k -> key != null && key.equalsIgnoreCase(k));
            out.put(key, sensitive ? MASK : e.getValue());
        }
        return out;
    }
}
