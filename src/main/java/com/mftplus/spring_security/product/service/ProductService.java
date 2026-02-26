package com.mftplus.spring_security.product.service;

import com.mftplus.spring_security.product.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    ProductDto save(ProductDto productDto, Long userId); // ⚠️ userId اضافه شد

    ProductDto update(ProductDto productDto);

    void deleteById(Long id);

    ProductDto findById(Long id);

    List<ProductDto> findAll();

    Page<ProductDto> findAll(Pageable pageable);

    // ⚠️ متدهای جدید
    List<ProductDto> findByUserId(Long userId);
    Page<ProductDto> findByUserId(Long userId, Pageable pageable);

    Page<ProductDto> findAllDeleted(Pageable pageable);

    Page<ProductDto> findAllEvenDeleted(Pageable pageable);

    void restoreById(Long id);

    Page<ProductDto> findByName(String name, Pageable pageable);
}