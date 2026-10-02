package com.streetconnect.repository;

import com.streetconnect.entity.Support;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupportRepository extends JpaRepository<Support, Long> {
    Optional<Support> findByComplaintIdAndUserId(Long complaintId, Long userId);
    Boolean existsByComplaintIdAndUserId(Long complaintId, Long userId);
    long countByComplaintId(Long complaintId);
    void deleteByComplaintIdAndUserId(Long complaintId, Long userId);
}
