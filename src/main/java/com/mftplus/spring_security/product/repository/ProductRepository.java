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

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find all soft-deleted products
    @Query(value = "SELECT * FROM products WHERE deleted = true",
            countQuery = "SELECT count(*) FROM products WHERE deleted = true",
            nativeQuery = true)
    Page<Product> findAllDeleted(Pageable pageable);

    // Find all products including deleted ones
    @Query(value = "SELECT * FROM products",
            countQuery = "SELECT count(*) FROM products",
            nativeQuery = true)
    Page<Product> findAllEvenDeleted(Pageable pageable);

    // Restore a soft-deleted product by id
    @Transactional
    @Modifying
    @Query(value = "UPDATE products SET deleted = false WHERE id = :id", nativeQuery = true)
    void restoreById(@Param("id") Long id);

    // Search products by name (case-insensitive, partial match)
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Search products by username (case-insensitive, partial match)
    Page<Product> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
}