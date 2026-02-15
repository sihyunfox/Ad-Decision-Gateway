package com.adg.admin.app;

import com.adg.shared.adapter.persistence.CreativeEntity;
import com.adg.shared.adapter.persistence.CreativeRepository;
import com.adg.shared.dto.CreativeDto;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 광고 소재(Creative) CRUD 서비스.
 */
@Service
public class CreativeService {

    private static final String DEFAULT_STATUS = "ACTIVE";

    private final CreativeRepository creativeRepository;

    public CreativeService(CreativeRepository creativeRepository) {
        this.creativeRepository = creativeRepository;
    }

    public List<CreativeDto> list(String campaignId, String status) {
        List<CreativeEntity> list;
        if (campaignId != null && !campaignId.isBlank()) {
            list = creativeRepository.findByCampaignId(campaignId);
        } else if (status != null && !status.isBlank()) {
            list = creativeRepository.findByStatus(status);
        } else {
            list = creativeRepository.findAll();
        }
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Cacheable(value = "creatives", key = "#creativeId", unless = "#result == null")
    public CreativeDto getByCreativeId(String creativeId) {
        return creativeRepository.findByCreativeId(creativeId)
                .map(this::toDto)
                .orElse(null);
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "creatives", key = "#dto.creativeId")
    public CreativeDto create(CreativeDto dto) {
        CreativeEntity entity = CreativeEntity.builder()
                .creativeId(dto.getCreativeId())
                .campaignId(dto.getCampaignId())
                .name(dto.getName())
                .format(dto.getFormat() != null ? dto.getFormat() : "banner")
                .width(dto.getWidth())
                .height(dto.getHeight())
                .mimeType(dto.getMimeType())
                .admSnippet(dto.getAdmSnippet())
                .landingUrl(dto.getLandingUrl())
                .status(dto.getStatus() != null ? dto.getStatus() : DEFAULT_STATUS)
                .updatedAt(Instant.now())
                .build();
        entity = creativeRepository.save(entity);
        return toDto(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "creatives", allEntries = true)
    public CreativeDto update(Long id, CreativeDto dto) {
        CreativeEntity existing = creativeRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setCreativeId(dto.getCreativeId() != null ? dto.getCreativeId() : existing.getCreativeId());
        existing.setCampaignId(dto.getCampaignId() != null ? dto.getCampaignId() : existing.getCampaignId());
        existing.setName(dto.getName());
        existing.setFormat(dto.getFormat() != null ? dto.getFormat() : existing.getFormat());
        existing.setWidth(dto.getWidth());
        existing.setHeight(dto.getHeight());
        existing.setMimeType(dto.getMimeType());
        existing.setAdmSnippet(dto.getAdmSnippet());
        existing.setLandingUrl(dto.getLandingUrl());
        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }
        existing = creativeRepository.save(existing);
        return toDto(existing);
    }

    private CreativeDto toDto(CreativeEntity e) {
        return CreativeDto.builder()
                .id(e.getId())
                .creativeId(e.getCreativeId())
                .campaignId(e.getCampaignId())
                .name(e.getName())
                .format(e.getFormat())
                .width(e.getWidth())
                .height(e.getHeight())
                .mimeType(e.getMimeType())
                .admSnippet(e.getAdmSnippet())
                .landingUrl(e.getLandingUrl())
                .status(e.getStatus())
                .build();
    }
}
