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
    private final UserRepository userRepository; // ⚠️ اضافه شد

    @Override
    public ProductDto save(ProductDto dto, Long userId) { // ⚠️ userId اضافه شد
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Product product = mapper.toEntity(dto);
        product.setUser(user);
        Product saved = repository.save(product);

        ProductDto result = mapper.toDto(saved);
        enrichDto(result, saved); // ⚠️ اضافه شد
        return result;
    }

    @Override
    public ProductDto update(ProductDto dto) {
        findById(dto.getId());
        Product product = mapper.toEntity(dto);
        Product updated = repository.save(product);

        ProductDto result = mapper.toDto(updated);
        enrichDto(result, updated); // ⚠️ اضافه شد
        return result;
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
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));

        ProductDto dto = mapper.toDto(product);
        enrichDto(dto, product); // ⚠️ اضافه شد
        return dto;
    }

    @Override
    public List<ProductDto> findAll() {
        return repository.findAll().stream()
                .map(product -> {
                    ProductDto dto = mapper.toDto(product);
                    enrichDto(dto, product); // ⚠️ اضافه شد
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductDto> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(product -> {
                    ProductDto dto = mapper.toDto(product);
                    enrichDto(dto, product); // ⚠️ اضافه شد
                    return dto;
                });
    }

    // ⚠️ متدهای جدید
    @Override
    public List<ProductDto> findByUserId(Long userId) {
        return repository.findByUserId(userId).stream()
                .map(product -> {
                    ProductDto dto = mapper.toDto(product);
                    enrichDto(dto, product);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductDto> findByUserId(Long userId, Pageable pageable) {
        return repository.findByUserId(userId, pageable)
                .map(product -> {
                    ProductDto dto = mapper.toDto(product);
                    enrichDto(dto, product);
                    return dto;
                });
    }

    @Override
    public Page<ProductDto> findAllDeleted(Pageable pageable) {
        return repository.findAllDeleted(pageable)
                .map(product -> {
                    ProductDto dto = mapper.toDto(product);
                    enrichDto(dto, product); // ⚠️ اضافه شد
                    return dto;
                });
    }

    @Override
    public Page<ProductDto> findAllEvenDeleted(Pageable pageable) {
        return repository.findAllEvenDeleted(pageable)
                .map(product -> {
                    ProductDto dto = mapper.toDto(product);
                    enrichDto(dto, product); // ⚠️ اضافه شد
                    return dto;
                });
    }

    @Override
    public void restoreById(Long id) {
        repository.restoreById(id);
    }

    @Override
    public Page<ProductDto> findByName(String name, Pageable pageable) {
        return repository.findByNameContainingIgnoreCase(name, pageable)
                .map(product -> {
                    ProductDto dto = mapper.toDto(product);
                    enrichDto(dto, product); // ⚠️ اضافه شد
                    return dto;
                });
    }

    // ⚠️ متد جدید برای پر کردن اطلاعات User
    private void enrichDto(ProductDto dto, Product product) {
        if (product.getUser() != null) {
            dto.setUserId(product.getUser().getId());
            dto.setUsername(product.getUser().getUsername());

            if (product.getUser().getPerson() != null) {
                String fullName = product.getUser().getPerson().getFirstName() + " " +
                        product.getUser().getPerson().getLastName();
                dto.setUserFullName(fullName);
            } else {
                dto.setUserFullName(product.getUser().getUsername());
            }
        }
    }
}