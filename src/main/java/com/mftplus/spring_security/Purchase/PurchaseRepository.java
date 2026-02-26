package com.mftplus.spring_security.Purchase;

import com.mftplus.spring_security.purchase.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    // پیدا کردن خریدهای یک کاربر
    List<Purchase> findByUserId(Long userId);

    // پیدا کردن خریدهای فعال یک کاربر (مثل SimCard)
    List<Purchase> findByUserIdOrderByPurchasedAtDesc(Long userId);

    // شمارش خریدهای یک کاربر
    @Query("SELECT COUNT(p) FROM Purchase p WHERE p.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}