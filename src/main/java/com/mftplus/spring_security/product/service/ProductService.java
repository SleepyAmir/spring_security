package com.mftplus.spring_security.product.service;

import com.mftplus.spring_security.product.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    ProductDto save(ProductDto productDto);

    ProductDto update(ProductDto productDto);

    void deleteById(Long id);

    ProductDto findById(Long id);

    List<ProductDto> findAll();

    Page<ProductDto> findAll(Pageable pageable);

    Page<ProductDto> findAllDeleted(Pageable pageable);

    Page<ProductDto> findAllEvenDeleted(Pageable pageable);

    void restoreById(Long id);

    Page<ProductDto> findByName(String name, Pageable pageable);

    Page<ProductDto> findByUsername(String username, Pageable pageable);
}