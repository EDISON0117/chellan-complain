package com.streetconnect.repository;

import com.streetconnect.entity.Role;
import com.streetconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByMobileNumber(String mobileNumber);
    Optional<User> findByEmailOrMobileNumber(String email, String mobileNumber);
    Boolean existsByEmail(String email);
    Boolean existsByMobileNumber(String mobileNumber);
    List<User> findByStreetId(Long streetId);
    List<User> findByRole(Role role);
    long countByStreetId(Long streetId);
    long countByRole(Role role);
}
