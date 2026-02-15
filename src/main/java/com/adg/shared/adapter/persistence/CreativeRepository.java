package com.adg.shared.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreativeRepository extends JpaRepository<CreativeEntity, Long> {

    Optional<CreativeEntity> findByCreativeId(String creativeId);

    List<CreativeEntity> findByCampaignId(String campaignId);

    List<CreativeEntity> findByStatus(String status);
}
