package com.adg.admin.app;

import com.adg.shared.adapter.persistence.AppEntity;
import com.adg.shared.adapter.persistence.AppRepository;
import com.adg.shared.dto.AppDto;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 앱(App) CRUD 서비스.
 */
@Service
public class AppService {

    private final AppRepository appRepository;

    public AppService(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    public List<AppDto> list(Long publisherId) {
        List<AppEntity> list = publisherId != null
                ? appRepository.findByPublisherId(publisherId)
                : appRepository.findAll();
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Cacheable(value = "apps", key = "#appId", unless = "#result == null")
    public AppDto getByAppId(String appId) {
        return appRepository.findByAppId(appId)
                .map(this::toDto)
                .orElse(null);
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "apps", key = "#dto.appId")
    public AppDto create(AppDto dto) {
        AppEntity entity = AppEntity.builder()
                .appId(dto.getAppId())
                .publisherId(dto.getPublisherId())
                .name(dto.getName())
                .bundle(dto.getBundle())
                .domain(dto.getDomain())
                .storeurl(dto.getStoreurl())
                .ver(dto.getVer())
                .extJson(dto.getExtJson())
                .updatedAt(Instant.now())
                .build();
        entity = appRepository.save(entity);
        return toDto(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "apps", key = "#result != null ? #result.appId : #dto.appId")
    public AppDto update(Long id, AppDto dto) {
        AppEntity existing = appRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setAppId(dto.getAppId() != null ? dto.getAppId() : existing.getAppId());
        existing.setPublisherId(dto.getPublisherId());
        existing.setName(dto.getName());
        existing.setBundle(dto.getBundle());
        existing.setDomain(dto.getDomain());
        existing.setStoreurl(dto.getStoreurl());
        existing.setVer(dto.getVer());
        existing.setExtJson(dto.getExtJson());
        existing = appRepository.save(existing);
        return toDto(existing);
    }

    private AppDto toDto(AppEntity e) {
        return AppDto.builder()
                .id(e.getId())
                .appId(e.getAppId())
                .publisherId(e.getPublisherId())
                .name(e.getName())
                .bundle(e.getBundle())
                .domain(e.getDomain())
                .storeurl(e.getStoreurl())
                .ver(e.getVer())
                .extJson(e.getExtJson())
                .build();
    }
}
