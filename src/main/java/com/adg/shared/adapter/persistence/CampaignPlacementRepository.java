package com.adg.shared.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignPlacementRepository extends JpaRepository<CampaignPlacementEntity, Long> {

    List<CampaignPlacementEntity> findByPlacementId(String placementId);
}
