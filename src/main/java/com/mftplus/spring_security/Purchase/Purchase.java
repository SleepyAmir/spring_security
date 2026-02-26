package com.mftplus.spring_security.purchase.model;

import com.mftplus.spring_security.bankAccount.model.entity.BankAccount;
import com.mftplus.spring_security.core.model.User;
import com.mftplus.spring_security.home.model.entity.Home;
import com.mftplus.spring_security.product.model.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "purchases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    // ⚠️ نوع خرید: PRODUCT یا HOME
    @Column(nullable = false, length = 20)
    private String purchaseType;

    // خریدار
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // حساب بانکی
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id", nullable = false)
    private BankAccount bankAccount;

    // ⚠️ ارتباط با Product (اختیاری)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // ⚠️ ارتباط با Home (اختیاری)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_id")
    private Home home;

    @PrePersist
    protected void onCreate() {
        purchasedAt = LocalDateTime.now();
    }
}