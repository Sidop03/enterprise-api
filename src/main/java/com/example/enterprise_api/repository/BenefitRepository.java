package com.example.enterprise_api.repository;

import com.example.enterprise_api.domain.Benefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BenefitRepository extends JpaRepository<Benefit, String> {
    @Query("SELECT b FROM Benefit b WHERE b.user.id = :userId ORDER BY b.eligibilityStatus DESC, b.name ASC")
    List<Benefit> findByUserId(@Param("userId") String userId);
    
    @Query("SELECT b FROM Benefit b WHERE b.user.id = :userId AND b.eligibilityStatus = 'ACTIVE'")
    List<Benefit> findActiveByUserId(@Param("userId") String userId);
}
