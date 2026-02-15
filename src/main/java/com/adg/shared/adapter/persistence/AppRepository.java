package com.adg.shared.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppRepository extends JpaRepository<AppEntity, Long> {

    Optional<AppEntity> findByAppId(String appId);

    List<AppEntity> findByPublisherId(Long publisherId);
}
