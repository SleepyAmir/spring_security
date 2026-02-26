package com.mftplus.spring_security.controller;

import com.mftplus.spring_security.core.security.SecurityUser;
import com.mftplus.spring_security.product.dto.ProductDto;
import com.mftplus.spring_security.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    // ⚠️ متد کمکی برای گرفتن userId
    private Long getCurrentUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
            return ((SecurityUser) authentication.getPrincipal()).getId();
        }
        throw new IllegalStateException("User not authenticated");
    }

    @PostMapping
    public ResponseEntity<ProductDto> create(@Valid @RequestBody ProductDto dto,
                                             Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        ProductDto created = service.save(dto, userId); // ⚠️ userId اضافه شد
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(@PathVariable Long id,
                                             @Valid @RequestBody ProductDto dto) {
        dto.setId(id);
        ProductDto updated = service.update(dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(@PathVariable Long id) {
        ProductDto found = service.findById(id);
        return ResponseEntity.ok(found);
    }

    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAll(
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ProductDto> page = service.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/deleted")
    public ResponseEntity<Page<ProductDto>> getAllDeleted(
            @PageableDefault(sort = "id") Pageable pageable) {
        Page<ProductDto> page = service.findAllDeleted(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/all-even-deleted")
    public ResponseEntity<Page<ProductDto>> getAllEvenDeleted(
            @PageableDefault(sort = "id") Pageable pageable) {
        Page<ProductDto> page = service.findAllEvenDeleted(pageable);
        return ResponseEntity.ok(page);
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable Long id) {
        service.restoreById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search/by-name")
    public ResponseEntity<Page<ProductDto>> searchByName(
            @RequestParam String name,
            @PageableDefault(sort = "name") Pageable pageable) {
        Page<ProductDto> page = service.findByName(name, pageable);
        return ResponseEntity.ok(page);
    }

    // ⚠️ متد جدید: جستجو بر اساس userId
    @GetMapping("/my-products")
    public ResponseEntity<Page<ProductDto>> getMyProducts(
            Authentication authentication,
            @PageableDefault(sort = "id") Pageable pageable) {
        Long userId = getCurrentUserId(authentication);
        Page<ProductDto> page = service.findByUserId(userId, pageable);
        return ResponseEntity.ok(page);
    }
}