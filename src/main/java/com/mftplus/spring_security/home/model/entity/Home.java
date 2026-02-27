package com.mftplus.spring_security.home.model.entity;

import com.mftplus.spring_security.core.model.Purchasable;
import com.mftplus.spring_security.core.model.User; // ⚠️ تغییر از Person
import com.mftplus.spring_security.home.model.enums.HomeStatus;
import com.mftplus.spring_security.home.model.enums.HomeType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "homes")
@SQLDelete(sql = "UPDATE homes SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Home implements Purchasable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 255)
    @Column(name = "address", nullable = false)
    private String address;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 100)
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @NotBlank(message = "State is required")
    @Size(min = 2, max = 100)
    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Postal code must be 10 digits")
    @Column(name = "postal_code", nullable = false, length = 10)
    private String postalCode;

    @NotNull(message = "Type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private HomeType type;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private HomeStatus status;

    @NotNull(message = "Area is required")
    @DecimalMin(value = "5.0", message = "Area must be at least 5 m2")
    @DecimalMax(value = "10000.0", message = "Area cannot exceed 10,000 m2")
    @Column(name = "area_sqm", nullable = false)
    private Double area;

    @NotNull(message = "Number of rooms is required")
    @Min(1) @Max(50)
    @Column(name = "number_of_rooms", nullable = false)
    private Integer numberOfRooms;

    @NotNull(message = "Floor is required")
    @Min(0) @Max(200)
    @Column(name = "floor", nullable = false)
    private Integer floor;

    @NotNull(message = "Total floors is required")
    @Min(1) @Max(200)
    @Column(name = "total_floors", nullable = false)
    private Integer totalFloors;

    @Min(1800)
    @Column(name = "year_built")
    private Integer yearBuilt;

    @Column(name = "has_parking", nullable = false)
    @Builder.Default
    private Boolean hasParking = false;

    @Column(name = "has_elevator", nullable = false)
    @Builder.Default
    private Boolean hasElevator = false;

    @Column(name = "has_storage", nullable = false)
    @Builder.Default
    private Boolean hasStorage = false;

    @Column(name = "has_balcony", nullable = false)
    @Builder.Default
    private Boolean hasBalcony = false;

    @Column(name = "has_pool", nullable = false)
    @Builder.Default
    private Boolean hasPool = false;

    @Column(name = "has_gym", nullable = false)
    @Builder.Default
    private Boolean hasGym = false;

    @NotNull(message = "Price is required")
    @Min(value = 1, message = "Price must be greater than 0")
    @Column(name = "price", nullable = false)
    private Long price;

    @Size(max = 1000)
    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted", nullable = false)
    @Builder.Default
    private boolean deleted = false;


    @NotNull(message = "Owner is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_home_user"))
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status      == null) status      = HomeStatus.AVAILABLE;
        if (hasParking  == null) hasParking  = false;
        if (hasElevator == null) hasElevator = false;
        if (hasStorage  == null) hasStorage  = false;
        if (hasBalcony  == null) hasBalcony  = false;
        if (hasPool     == null) hasPool     = false;
        if (hasGym      == null) hasGym      = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public String getName() {
        return "Home in " + city + " - " + type;
    }

    @Override
    public Double getPrice() {
        return price != null ? price.doubleValue() : 0.0;
    }
}