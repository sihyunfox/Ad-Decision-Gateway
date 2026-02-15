package com.adg.shared.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BidResponseRepository extends JpaRepository<BidResponseEntity, Long> {
}
