package com.adg.shared.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CapAllowanceRepository extends JpaRepository<CapAllowanceEntity, CapAllowanceId> {

    List<CapAllowanceEntity> findByClientIdAndUserId(String clientId, String userId);
}
