package com.mftplus.spring_security.home.repository;

import com.mftplus.spring_security.home.model.entity.Home;
import com.mftplus.spring_security.home.model.enums.HomeStatus;
import com.mftplus.spring_security.home.model.enums.HomeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HomeRepository extends JpaRepository<Home, Long> {

    List<Home> findAllByPersonId(Long personId);

    Page<Home> findAllByPersonId(Long personId, Pageable pageable);

    Page<Home> findAllByStatus(HomeStatus status, Pageable pageable);

    Page<Home> findAllByType(HomeType type, Pageable pageable);

    @Query("SELECT h FROM Home h WHERE " +
            "(:city   IS NULL OR LOWER(h.city)  LIKE LOWER(CONCAT('%',:city,'%'))) AND " +
            "(:state  IS NULL OR LOWER(h.state) LIKE LOWER(CONCAT('%',:state,'%'))) AND " +
            "(:status IS NULL OR h.status = :status) AND " +
            "(:type   IS NULL OR h.type   = :type)")
    Page<Home> findAllByFilter(@Param("city")   String city,
                               @Param("state")  String state,
                               @Param("status") HomeStatus status,
                               @Param("type")   HomeType type,
                               Pageable pageable);

    @Query(value      = "SELECT * FROM homes WHERE deleted = true",
            countQuery = "SELECT COUNT(*) FROM homes WHERE deleted = true",
            nativeQuery = true)
    Page<Home> findAllDeleted(Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "UPDATE homes SET deleted = false WHERE id = :id", nativeQuery = true)
    void restoreById(@Param("id") Long id);
}