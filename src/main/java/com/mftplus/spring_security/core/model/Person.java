package com.mftplus.spring_security.core.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "persons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "نام الزامی است")
    @Size(min = 2, max = 50, message = "نام باید بین 2 تا 50 کاراکتر باشد")
    @Column(nullable = false, length = 50)
    private String firstName;
    
    @NotBlank(message = "نام خانوادگی الزامی است")
    @Size(min = 2, max = 50, message = "نام خانوادگی باید بین 2 تا 50 کاراکتر باشد")
    @Column(nullable = false, length = 50)
    private String lastName;
    
    @NotBlank(message = "کد ملی الزامی است")
    @Pattern(regexp = "^[0-9]{10}$", message = "کد ملی باید 10 رقم باشد")
    @Column(unique = true, nullable = false, length = 10)
    private String nationalCode;
    
    @Email(message = "ایمیل معتبر نیست")
    @Size(max = 100, message = "ایمیل نمی‌تواند بیشتر از 100 کاراکتر باشد")
    @Column(length = 100)
    private String email;
    
    @Pattern(regexp = "^09[0-9]{9}$", message = "شماره موبایل معتبر نیست")
    @Column(length = 11)
    private String mobile;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
    
    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private User user;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
}

