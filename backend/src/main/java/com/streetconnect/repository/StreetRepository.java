package com.streetconnect.repository;

import com.streetconnect.entity.Street;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StreetRepository extends JpaRepository<Street, Long> {
    Optional<Street> findByNameAndAreaAndCity(String name, String area, String city);
    List<Street> findAllByOrderByCityAscNameAsc();
    List<Street> findByCityIgnoreCase(String city);
}
