package com.mftplus.spring_security.product.service;

import com.mftplus.spring_security.core.model.User;
import com.mftplus.spring_security.core.repository.UserRepository;
import com.mftplus.spring_security.product.dto.ProductDto;
import com.mftplus.spring_security.product.exception.ProductNotFoundException;
import com.mftplus.spring_security.product.mapper.ProductMapper;
import com.mftplus.spring_security.product.model.Product;
import com.mftplus.spring_security.product.repository.ProductRepository;
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
    private final UserRepository userRepository;

    @Override
    public ProductDto save(ProductDto dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Product product = mapper.toEntity(dto);
        product.setUser(user);
        return mapper.toDto(repository.save(product));
    }

    @Override
    public ProductDto update(ProductDto dto) {
        findById(dto.getId());
        Product product = mapper.toEntity(dto);
        return mapper.toDto(repository.save(product));
    }

    @Override
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new ProductNotFoundException("Product with id " + id + " not found");
        }
        repository.deleteById(id);
    }

    @Override
    public ProductDto findById(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found")));
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
    public List<ProductDto> findByUserId(Long userId) {
        return repository.findByUserId(userId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductDto> findByUserId(Long userId, Pageable pageable) {
        return repository.findByUserId(userId, pageable).map(mapper::toDto);
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
}