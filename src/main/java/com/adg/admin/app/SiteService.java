package com.adg.admin.app;

import com.adg.shared.adapter.persistence.SiteEntity;
import com.adg.shared.adapter.persistence.SiteRepository;
import com.adg.shared.dto.SiteDto;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 사이트(Site) CRUD 서비스.
 */
@Service
public class SiteService {

    private final SiteRepository siteRepository;

    public SiteService(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    public List<SiteDto> list(Long publisherId) {
        List<SiteEntity> list = publisherId != null
                ? siteRepository.findByPublisherId(publisherId)
                : siteRepository.findAll();
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Cacheable(value = "sites", key = "#siteId", unless = "#result == null")
    public SiteDto getBySiteId(String siteId) {
        return siteRepository.findBySiteId(siteId)
                .map(this::toDto)
                .orElse(null);
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sites", key = "#dto.siteId")
    public SiteDto create(SiteDto dto) {
        SiteEntity entity = SiteEntity.builder()
                .siteId(dto.getSiteId())
                .publisherId(dto.getPublisherId())
                .name(dto.getName())
                .domain(dto.getDomain())
                .page(dto.getPage())
                .ref(dto.getRef())
                .extJson(dto.getExtJson())
                .updatedAt(Instant.now())
                .build();
        entity = siteRepository.save(entity);
        return toDto(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sites", key = "#result != null ? #result.siteId : #dto.siteId")
    public SiteDto update(Long id, SiteDto dto) {
        SiteEntity existing = siteRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setSiteId(dto.getSiteId() != null ? dto.getSiteId() : existing.getSiteId());
        existing.setPublisherId(dto.getPublisherId());
        existing.setName(dto.getName());
        existing.setDomain(dto.getDomain());
        existing.setPage(dto.getPage());
        existing.setRef(dto.getRef());
        existing.setExtJson(dto.getExtJson());
        existing = siteRepository.save(existing);
        return toDto(existing);
    }

    private SiteDto toDto(SiteEntity e) {
        return SiteDto.builder()
                .id(e.getId())
                .siteId(e.getSiteId())
                .publisherId(e.getPublisherId())
                .name(e.getName())
                .domain(e.getDomain())
                .page(e.getPage())
                .ref(e.getRef())
                .extJson(e.getExtJson())
                .build();
    }
}
