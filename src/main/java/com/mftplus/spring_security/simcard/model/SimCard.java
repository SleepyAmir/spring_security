package com.mftplus.spring_security.simcard.model;

import com.mftplus.spring_security.core.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "simcards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimCard {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Pattern(regexp = "^09[0-9]{9}$", message = "شماره سیم کارت معتبر نیست")
    @Column(unique = true, nullable = false, length = 11)
    private String phoneNumber;
    
    @Size(max = 20, message = "شماره سریال نمی‌تواند بیشتر از 20 کاراکتر باشد")
    @Column(length = 20)
    private String serialNumber;
    
    @Size(max = 50, message = "اپراتور نمی‌تواند بیشتر از 50 کاراکتر باشد")
    @Column(length = 50)
    private String operator;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;
    
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
}

