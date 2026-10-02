package com.streetconnect.repository;

import com.streetconnect.entity.Complaint;
import com.streetconnect.entity.ComplaintStatus;
import com.streetconnect.entity.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByStreetIdOrderByCreatedAtDesc(Long streetId);

    List<Complaint> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByStatus(ComplaintStatus status);

    long countByStreetIdAndStatus(Long streetId, ComplaintStatus status);

    long countByStreetId(Long streetId);

    @Query("SELECT c FROM Complaint c WHERE " +
           "(:streetId IS NULL OR c.street.id = :streetId) AND " +
           "(:categoryId IS NULL OR c.category.id = :categoryId) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:priority IS NULL OR c.priority = :priority) AND " +
           "(:search IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.landmark) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY c.createdAt DESC")
    List<Complaint> searchAndFilter(
            @Param("streetId") Long streetId,
            @Param("categoryId") Long categoryId,
            @Param("status") ComplaintStatus status,
            @Param("priority") Priority priority,
            @Param("search") String search
    );

    @Query("SELECT c.category.name, COUNT(c) FROM Complaint c GROUP BY c.category.name")
    List<Object[]> countByCategory();

    @Query("SELECT c.status, COUNT(c) FROM Complaint c GROUP BY c.status")
    List<Object[]> countByStatusGroup();

    @Query("SELECT c.street.name, COUNT(c) FROM Complaint c GROUP BY c.street.name ORDER BY COUNT(c) DESC")
    List<Object[]> countByStreetTop();
}
