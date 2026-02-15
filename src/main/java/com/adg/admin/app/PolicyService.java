package com.adg.admin.app;

import com.adg.shared.adapter.persistence.AuditLogPortAdapter;
import com.adg.shared.adapter.persistence.PolicyEntity;
import com.adg.shared.adapter.persistence.PolicyRepository;
import com.adg.shared.dto.PolicyDto;
import com.adg.shared.port.EventQueuePort;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 정책(policy) CRUD 및 감사(audit) 로깅 서비스.
 */
@Service
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final AuditLogPortAdapter auditLog;
    private final EventQueuePort eventQueuePort;

    public PolicyService(PolicyRepository policyRepository, AuditLogPortAdapter auditLog, EventQueuePort eventQueuePort) {
        this.policyRepository = policyRepository;
        this.auditLog = auditLog;
        this.eventQueuePort = eventQueuePort;
    }

    public List<PolicyDto> listActive(String clientId) {
        return policyRepository.findByClientIdAndActiveTrue(clientId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<PolicyDto> listAll() {
        return policyRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<PolicyDto> listActivePolicies() {
        return policyRepository.findByActiveTrue().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "policies", key = "#id", unless = "#result == null")
    public PolicyDto getById(Long id) {
        return policyRepository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "policies", key = "#result != null ? #result.id : #dto.id")
    public PolicyDto update(PolicyDto dto, String actor) {
        PolicyEntity existing = dto.getId() != null ? policyRepository.findById(dto.getId()).orElse(null) : null;
        String oldValue = existing != null ? existing.getFilterRules() + "|" + existing.getAbFlags() : null;

        PolicyEntity entity;
        if (existing != null) {
            existing.setClientId(dto.getClientId());
            existing.setFilterRules(dto.getFilterRules());
            existing.setAbFlags(dto.getAbFlags());
            existing.setTimeoutConfig(dto.getTimeoutConfig());
            existing.setActive(dto.isActive());
            entity = policyRepository.save(existing);
        } else {
            entity = PolicyEntity.builder()
                    .clientId(dto.getClientId())
                    .filterRules(dto.getFilterRules())
                    .abFlags(dto.getAbFlags())
                    .timeoutConfig(dto.getTimeoutConfig())
                    .active(dto.isActive())
                    .build();
            entity.setUpdatedAt(java.time.Instant.now());
            entity = policyRepository.save(entity);
        }
        PolicyEntity saved = entity;
        String newValue = saved.getFilterRules() + "|" + saved.getAbFlags();
        auditLog.log("policy", String.valueOf(saved.getId()), existing != null ? "UPDATE" : "CREATE", oldValue, newValue, actor);
        String payload = String.format("{\"policyId\":%d,\"clientId\":\"%s\"}", saved.getId(), saved.getClientId());
        eventQueuePort.append("policy-updated", payload);
        return toDto(saved);
    }

    private PolicyDto toDto(PolicyEntity e) {
        return PolicyDto.builder()
                .id(e.getId())
                .clientId(e.getClientId())
                .filterRules(e.getFilterRules())
                .abFlags(e.getAbFlags())
                .timeoutConfig(e.getTimeoutConfig())
                .active(e.isActive())
                .build();
    }
}
