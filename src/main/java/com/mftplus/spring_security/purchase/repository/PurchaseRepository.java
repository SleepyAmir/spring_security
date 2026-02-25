package com.mftplus.spring_security.purchase.repository;

import com.mftplus.spring_security.purchase.model.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {


    // همه خریدهای یک کاربر — برای پروفایل
    List<Purchase> findByUserIdOrderByPurchasedAtDesc(Long userId);

    // چک میکنیم کاربر قبلاً این کالا رو خریده یا نه
    boolean existsByUserIdAndProductId(Long userId, Long productId);

}
