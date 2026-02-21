package com.mftplus.spring_security.simcard.repository;

import com.mftplus.spring_security.simcard.model.SimCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SimCardRepository extends JpaRepository<SimCard, Long> {
    Optional<SimCard> findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
    List<SimCard> findByOwnerId(Long ownerId);
    List<SimCard> findByOwnerIdAndActiveTrue(Long ownerId);
    
    @Query("SELECT COUNT(s) FROM SimCard s WHERE s.owner.id = :userId AND s.active = true")
    long countActiveSimCardsByOwnerId(@Param("userId") Long userId);
}

