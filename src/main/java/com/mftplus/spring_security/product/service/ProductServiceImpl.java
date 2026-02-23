package com.mftplus.spring_security.product.service;

import com.mftplus.spring_security.product.dto.ProductDto;
import com.mftplus.spring_security.product.exception.ProductNotFoundException;
import com.mftplus.spring_security.product.mapper.ProductMapper;
import com.mftplus.spring_security.product.model.Product;
import com.mftplus.spring_security.product.repository.ProductRepository;
import com.mftplus.spring_security.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Override
    public ProductDto save(ProductDto dto) {
        Product product = mapper.toEntity(dto);
        Product saved = repository.save(product);
        return mapper.toDto(saved);
    }

    @Override
    public ProductDto update(ProductDto dto) {
        // Ensure the product exists before updating
        findById(dto.getId());  // throws exception if not found
        Product product = mapper.toEntity(dto);
        Product updated = repository.save(product);
        return mapper.toDto(updated);
    }

    @Override
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new ProductNotFoundException("Product with id " + id + " not found");
        }
        repository.deleteById(id);  // soft delete thanks to @SQLDelete
    }

    @Override
    public ProductDto findById(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));
        return mapper.toDto(product);
    }

    @Override
    public List<ProductDto> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductDto> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDto);
    }

    @Override
    public Page<ProductDto> findAllDeleted(Pageable pageable) {
        return repository.findAllDeleted(pageable).map(mapper::toDto);
    }

    @Override
    public Page<ProductDto> findAllEvenDeleted(Pageable pageable) {
        return repository.findAllEvenDeleted(pageable).map(mapper::toDto);
    }

    @Override
    public void restoreById(Long id) {
        repository.restoreById(id);
    }

    @Override
    public Page<ProductDto> findByName(String name, Pageable pageable) {
        return repository.findByNameContainingIgnoreCase(name, pageable)
                .map(mapper::toDto);
    }

    @Override
    public Page<ProductDto> findByUsername(String username, Pageable pageable) {
        return repository.findByUsernameContainingIgnoreCase(username, pageable)
                .map(mapper::toDto);
    }
}