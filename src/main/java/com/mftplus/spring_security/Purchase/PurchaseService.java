package com.mftplus.spring_security.Purchase;

import com.mftplus.spring_security.bankAccount.model.entity.BankAccount;
import com.mftplus.spring_security.bankAccount.repository.BankAccountRepository;
import com.mftplus.spring_security.core.model.Purchasable;
import com.mftplus.spring_security.core.model.User;
import com.mftplus.spring_security.core.repository.UserRepository;
import com.mftplus.spring_security.home.model.entity.Home;
import com.mftplus.spring_security.home.model.enums.HomeStatus;
import com.mftplus.spring_security.home.repository.HomeRepository;
import com.mftplus.spring_security.product.model.Product;
import com.mftplus.spring_security.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final ProductRepository productRepository;
    private final HomeRepository homeRepository;

    /**
     * خرید کالا
     */
    @Transactional
    public Purchase buyProduct(Long userId, Long productId, Long bankAccountId) {
        // پیدا کردن product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // فراخوانی متد کلی
        return buy(userId, product, "PRODUCT", bankAccountId, null);
    }

    /**
     * خرید خانه
     */
    @Transactional
    public Purchase buyHome(Long userId, Long homeId, Long bankAccountId) {
        // پیدا کردن home
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new IllegalArgumentException("Home not found"));

        // چک وضعیت
        if (home.getStatus() != HomeStatus.AVAILABLE) {
            throw new IllegalArgumentException("Home is not available");
        }

        // فراخوانی متد کلی
        return buy(userId, home, "HOME", bankAccountId, () -> {
            home.setStatus(HomeStatus.SOLD);
            homeRepository.save(home);
        });
    }

    /**
     * لیست خریدهای کاربر
     */
    public List<Purchase> findUserPurchases(Long userId) {
        return purchaseRepository.findByUserId(userId);
    }

    // ═══════════════════════════════════════════════════════════════════
    // Private Helper
    // ═══════════════════════════════════════════════════════════════════

    private Purchase buy(Long userId, Purchasable item, String type, Long bankAccountId, Runnable afterPurchase) {
        log.info("Processing purchase: userId={}, item={}, type={}", userId, item.getName(), type);

        // 1. پیدا کردن User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 2. پیدا کردن BankAccount
        BankAccount account = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Bank account not found"));

        // 3. چک مالکیت
        if (!account.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Account doesn't belong to you");
        }

        // 4. چک موجودی
        BigDecimal price = BigDecimal.valueOf(item.getPrice());
        if (account.getBalance().compareTo(price) < 0) {
            throw new IllegalArgumentException("Insufficient balance. Required: " + item.getPrice());
        }

        // 5. کسر موجودی
        account.setBalance(account.getBalance().subtract(price));
        bankAccountRepository.save(account);

        // 6. اجرای کار اضافی (مثلاً تغییر status خانه)
        if (afterPurchase != null) {
            afterPurchase.run();
        }

        // 7. ثبت خرید
        Purchase purchase = Purchase.builder()
                .invoiceNumber(generateInvoice())
                .price(item.getPrice())
                .itemName(item.getName())
                .purchaseType(type)
                .user(user)
                .bankAccount(account)
                .build();

        Purchase saved = purchaseRepository.save(purchase);
        log.info("Purchase successful. Invoice: {}", saved.getInvoiceNumber());

        return saved;
    }

    private String generateInvoice() {
        return "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}