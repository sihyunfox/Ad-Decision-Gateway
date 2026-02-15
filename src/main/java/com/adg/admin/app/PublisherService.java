package com.adg.admin.app;

import com.adg.shared.adapter.persistence.PublisherEntity;
import com.adg.shared.adapter.persistence.PublisherRepository;
import com.adg.shared.dto.PublisherDto;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 퍼블리셔(Publisher) CRUD 서비스.
 */
@Service
public class PublisherService {

    private final PublisherRepository publisherRepository;

    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    public List<PublisherDto> list() {
        return publisherRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "publishers", key = "#publisherId", unless = "#result == null")
    public PublisherDto getByPublisherId(String publisherId) {
        return publisherRepository.findByPublisherId(publisherId)
                .map(this::toDto)
                .orElse(null);
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "publishers", key = "#dto.publisherId")
    public PublisherDto create(PublisherDto dto) {
        PublisherEntity entity = PublisherEntity.builder()
                .publisherId(dto.getPublisherId())
                .name(dto.getName())
                .domain(dto.getDomain())
                .extJson(dto.getExtJson())
                .updatedAt(Instant.now())
                .build();
        entity = publisherRepository.save(entity);
        return toDto(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "publishers", key = "#result != null ? #result.publisherId : #dto.publisherId")
    public PublisherDto update(Long id, PublisherDto dto) {
        PublisherEntity existing = publisherRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setPublisherId(dto.getPublisherId() != null ? dto.getPublisherId() : existing.getPublisherId());
        existing.setName(dto.getName());
        existing.setDomain(dto.getDomain());
        existing.setExtJson(dto.getExtJson());
        existing = publisherRepository.save(existing);
        return toDto(existing);
    }

    private PublisherDto toDto(PublisherEntity e) {
        return PublisherDto.builder()
                .id(e.getId())
                .publisherId(e.getPublisherId())
                .name(e.getName())
                .domain(e.getDomain())
                .extJson(e.getExtJson())
                .build();
    }
}
