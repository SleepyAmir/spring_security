package com.mftplus.spring_security.Purchase;

import com.mftplus.spring_security.bankAccount.model.entity.BankAccount;
import com.mftplus.spring_security.core.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "purchases")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String invoiceNumber;

    @Column(nullable = false)
    private LocalDateTime purchasedAt;

    @Column(nullable = false)
    private Double price;

    // ⚠️ ساده: فقط نام محصول/خانه
    @Column(nullable = false, length = 255)
    private String itemName;

    // ⚠️ ساده: فقط TYPE (PRODUCT یا HOME)
    @Column(nullable = false, length = 20)
    private String purchaseType; // "PRODUCT" or "HOME"

    // خریدار
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // حساب بانکی
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id", nullable = false)
    private BankAccount bankAccount;

    @PrePersist
    protected void onCreate() {
        purchasedAt = LocalDateTime.now();
    }
}