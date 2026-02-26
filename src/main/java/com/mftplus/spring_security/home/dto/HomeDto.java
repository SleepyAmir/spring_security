package com.mftplus.spring_security.home.dto;

import com.mftplus.spring_security.home.model.enums.HomeStatus;
import com.mftplus.spring_security.home.model.enums.HomeType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeDto {

    private Long id;

    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 255)
    private String address;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 100)
    private String city;

    @NotBlank(message = "State is required")
    @Size(min = 2, max = 100)
    private String state;

    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Postal code must be 10 digits")
    private String postalCode;

    @NotNull(message = "Type is required")
    private HomeType type;

    @NotNull(message = "Status is required")
    private HomeStatus status;

    @NotNull(message = "Area is required")
    @DecimalMin(value = "5.0", message = "Area must be at least 5 m2")
    @DecimalMax(value = "10000.0", message = "Area cannot exceed 10,000 m2")
    private Double area;

    @NotNull(message = "Number of rooms is required")
    @Min(1) @Max(50)
    private Integer numberOfRooms;

    @NotNull(message = "Floor is required")
    @Min(0) @Max(200)
    private Integer floor;

    @NotNull(message = "Total floors is required")
    @Min(1) @Max(200)
    private Integer totalFloors;

    @Min(1800)
    private Integer yearBuilt;

    private Boolean hasParking  = false;
    private Boolean hasElevator = false;
    private Boolean hasStorage  = false;
    private Boolean hasBalcony  = false;
    private Boolean hasPool     = false;
    private Boolean hasGym      = false;

    @NotNull(message = "Price is required")
    @Min(value = 1, message = "Price must be greater than 0")
    private Long price;

    @Size(max = 1000)
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ⚠️ تغییرات
    @NotNull(message = "Owner is required")
    private Long userId; // تغییر از personId

    private String userFullName; // تغییر از personFullName
}