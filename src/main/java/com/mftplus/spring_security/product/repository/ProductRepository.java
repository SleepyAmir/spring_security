package com.mftplus.spring_security.product.repository;

import com.mftplus.spring_security.product.model.Product;
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
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ⚠️ متدهای جدید برای User
    List<Product> findByUserId(Long userId);
    Page<Product> findByUserId(Long userId, Pageable pageable);

    @Query(value = "SELECT * FROM products WHERE deleted = true",
            countQuery = "SELECT count(*) FROM products WHERE deleted = true",
            nativeQuery = true)
    Page<Product> findAllDeleted(Pageable pageable);

    @Query(value = "SELECT * FROM products",
            countQuery = "SELECT count(*) FROM products",
            nativeQuery = true)
    Page<Product> findAllEvenDeleted(Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "UPDATE products SET deleted = false WHERE id = :id", nativeQuery = true)
    void restoreById(@Param("id") Long id);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // ⚠️ این متد اشتباهه - Product فیلد username نداره - حذف شد
    // Page<Product> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
}