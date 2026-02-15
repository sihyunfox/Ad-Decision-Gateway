package com.adg.shared.adapter.persistence;

import com.adg.shared.dto.ProfileResponse;
import com.adg.shared.port.ProfilePort;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * ProfilePort 구현체. profiles 테이블 조회. 1분 캐시 적용.
 * app.decision.use-http-dependencies=false(기본)일 때 활성화.
 */
@Component
@ConditionalOnProperty(name = "app.decision.use-http-dependencies", havingValue = "false", matchIfMissing = true)
public class ProfilePersistenceAdapter implements ProfilePort {

    private final ProfileRepository repository;
    private final ObjectMapper objectMapper;

    public ProfilePersistenceAdapter(ProfileRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Cacheable(cacheNames = "profiles", cacheManager = "decisionCacheManager")
    public Optional<ProfileResponse> getProfile(String userId) {
        return repository.findByUserId(userId)
                .map(this::toResponse);
    }

    private ProfileResponse toResponse(ProfileEntity e) {
        return ProfileResponse.builder()
                .userId(e.getUserId())
                .segment(parseStringList(e.getSegment()))
                .interests(parseStringList(e.getInterests()))
                .riskScore(e.getRiskScore())
                .build();
    }

    private List<String> parseStringList(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}
