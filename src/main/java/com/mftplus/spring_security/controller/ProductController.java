package com.mftplus.spring_security.controller;

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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    // Create a new product
    @PostMapping
    public ResponseEntity<ProductDto> create(@Valid @RequestBody ProductDto dto) {
        ProductDto created = service.save(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Update an existing product
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(@PathVariable Long id, @Valid @RequestBody ProductDto dto) {
        dto.setId(id);  // ensure id in path matches DTO
        ProductDto updated = service.update(dto);
        return ResponseEntity.ok(updated);
    }

    // Get product by id
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(@PathVariable Long id) {
        ProductDto found = service.findById(id);
        return ResponseEntity.ok(found);
    }

    // Get all products (paginated)
    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAll(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ProductDto> page = service.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    // Soft delete product by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Get all deleted products
    @GetMapping("/deleted")
    public ResponseEntity<Page<ProductDto>> getAllDeleted(@PageableDefault(sort = "id") Pageable pageable) {
        Page<ProductDto> page = service.findAllDeleted(pageable);
        return ResponseEntity.ok(page);
    }

    // Get all products including deleted
    @GetMapping("/all-even-deleted")
    public ResponseEntity<Page<ProductDto>> getAllEvenDeleted(@PageableDefault(sort = "id") Pageable pageable) {
        Page<ProductDto> page = service.findAllEvenDeleted(pageable);
        return ResponseEntity.ok(page);
    }

    // Restore a deleted product
    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable Long id) {
        service.restoreById(id);
        return ResponseEntity.ok().build();
    }

    // Search by name (case-insensitive, partial match)
    @GetMapping("/search/by-name")
    public ResponseEntity<Page<ProductDto>> searchByName(@RequestParam String name,
                                                         @PageableDefault(sort = "name") Pageable pageable) {
        Page<ProductDto> page = service.findByName(name, pageable);
        return ResponseEntity.ok(page);
    }

    // Search by username
    @GetMapping("/search/by-username")
    public ResponseEntity<Page<ProductDto>> searchByUsername(@RequestParam String username,
                                                             @PageableDefault(sort = "username") Pageable pageable) {
        Page<ProductDto> page = service.findByUsername(username, pageable);
        return ResponseEntity.ok(page);
    }
}