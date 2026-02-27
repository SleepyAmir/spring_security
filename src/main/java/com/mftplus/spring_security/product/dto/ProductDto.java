package com.mftplus.spring_security.product.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductDto {

    private Long id;

    @Pattern(regexp = "^[a-zA-Z\\s]{3,20}$", message = "Invalid Name (only letters and spaces, 3-20 chars)")
    private String name;

    @Pattern(regexp = "^[a-zA-Z0-9\\s.,!?-]{3,255}$", message = "Invalid description (letters, numbers, punctuation, 3-255 chars)")
    private String description;

    @Positive(message = "Price must be positive")
    private Double price;

    private boolean deleted = false;

    // ⚠️ فیلدهای مربوط به User اضافه شد
    private Long userId;

    private String userFullName;
}