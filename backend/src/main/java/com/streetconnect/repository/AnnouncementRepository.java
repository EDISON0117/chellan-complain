package com.streetconnect.repository;

import com.streetconnect.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    @Query("SELECT a FROM Announcement a WHERE (a.street.id = :streetId OR a.street IS NULL) AND a.isActive = true ORDER BY a.createdAt DESC")
    List<Announcement> findActiveByStreetOrGlobal(@Param("streetId") Long streetId);

    List<Announcement> findAllByOrderByCreatedAtDesc();
}
