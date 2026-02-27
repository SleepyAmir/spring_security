package com.mftplus.spring_security.Purchase;

import com.mftplus.spring_security.bankAccount.model.entity.BankAccount;
import com.mftplus.spring_security.bankAccount.repository.BankAccountRepository;
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final ProductRepository productRepository;
    private final HomeRepository homeRepository;

    // ═══════════════════════════════════════════════════════════════════
    // خرید Product
    // ═══════════════════════════════════════════════════════════════════

    @Transactional
    public PurchaseDto buyProduct(Long userId, Long productId, Long bankAccountId) {
        log.info("Processing product purchase: userId={}, productId={}", userId, productId);

        // پیدا کردن Product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // پیدا کردن User
        User user = findUser(userId);

        // پیدا کردن BankAccount
        BankAccount account = findBankAccount(bankAccountId);

        // چک مالکیت حساب
        validateAccountOwnership(account, userId);

        // چک موجودی
        validateBalance(account, product.getPrice());

        // کسر موجودی
        account.setBalance(account.getBalance().subtract(BigDecimal.valueOf(product.getPrice())));
        bankAccountRepository.save(account);

        // ثبت خرید
        Purchase purchase = Purchase.builder()
                .invoiceNumber(generateInvoice())
                .price(product.getPrice())
                .purchaseType("PRODUCT")
                .user(user)
                .bankAccount(account)
                .product(product)
                .build();

        Purchase saved = purchaseRepository.save(purchase);
        log.info("Product purchase successful. Invoice: {}", saved.getInvoiceNumber());

        return convertToDto(saved);
    }

    // ═══════════════════════════════════════════════════════════════════
    // خرید Home
    // ═══════════════════════════════════════════════════════════════════

    @Transactional
    public PurchaseDto buyHome(Long userId, Long homeId, Long bankAccountId) {
        log.info("Processing home purchase: userId={}, homeId={}", userId, homeId);

        // پیدا کردن Home
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new IllegalArgumentException("Home not found"));

        // چک وضعیت
        if (home.getStatus() != HomeStatus.AVAILABLE) {
            throw new IllegalArgumentException("Home is not available for purchase");
        }

        // پیدا کردن User
        User user = findUser(userId);

        // پیدا کردن BankAccount
        BankAccount account = findBankAccount(bankAccountId);

        // چک مالکیت حساب
        validateAccountOwnership(account, userId);

        // چک موجودی
        validateBalance(account, home.getPrice());

        // کسر موجودی
        account.setBalance(account.getBalance().subtract(BigDecimal.valueOf(home.getPrice())));
        bankAccountRepository.save(account);

        // تغییر وضعیت Home
        home.setStatus(HomeStatus.SOLD);
        homeRepository.save(home);

        // ثبت خرید
        Purchase purchase = Purchase.builder()
                .invoiceNumber(generateInvoice())
                .price(home.getPrice().doubleValue())
                .purchaseType("HOME")
                .user(user)
                .bankAccount(account)
                .home(home)
                .build();

        Purchase saved = purchaseRepository.save(purchase);
        log.info("Home purchase successful. Invoice: {}", saved.getInvoiceNumber());

        return convertToDto(saved);
    }

    // ═══════════════════════════════════════════════════════════════════
    // لیست خریدهای کاربر (مثل SimCard)
    // ═══════════════════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public List<PurchaseDto> findAllByUserId(Long userId) {
        log.debug("Finding purchases for user: {}", userId);
        return purchaseRepository.findByUserIdOrderByPurchasedAtDesc(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<PurchaseDto> findById(Long id) {
        return purchaseRepository.findById(id)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Optional<PurchaseDto> findByIdAndUserId(Long id, Long userId) {
        return purchaseRepository.findById(id)
                .filter(purchase -> purchase.getUser() != null && purchase.getUser().getId().equals(userId))
                .map(this::convertToDto);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private Helper Methods
    // ─────────────────────────────────────────────────────────────────────────

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private BankAccount findBankAccount(Long accountId) {
        return bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Bank account not found"));
    }

    private void validateAccountOwnership(BankAccount account, Long userId) {
        if (!account.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("This account doesn't belong to you");
        }
    }

    private void validateBalance(BankAccount account, Double price) {
        if (account.getBalance().compareTo(BigDecimal.valueOf(price)) < 0) {
            throw new IllegalArgumentException("Insufficient balance. Required: " + price);
        }
    }

    private String generateInvoice() {
        return "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private PurchaseDto convertToDto(Purchase purchase) {
        PurchaseDto dto = new PurchaseDto();
        dto.setId(purchase.getId());
        dto.setInvoiceNumber(purchase.getInvoiceNumber());
        dto.setPurchasedAt(purchase.getPurchasedAt());
        dto.setPrice(purchase.getPrice());
        dto.setPurchaseType(purchase.getPurchaseType());

        // User info
        if (purchase.getUser() != null) {
            dto.setUserId(purchase.getUser().getId());
            dto.setUsername(purchase.getUser().getUsername());
        }

        // BankAccount info
        if (purchase.getBankAccount() != null) {
            dto.setBankAccountId(purchase.getBankAccount().getId());
            dto.setAccountNumber(purchase.getBankAccount().getAccountNumber());
        }

        // Item name
        if ("PRODUCT".equals(purchase.getPurchaseType()) && purchase.getProduct() != null) {
            dto.setItemName(purchase.getProduct().getName());
            dto.setProductId(purchase.getProduct().getId());
        } else if ("HOME".equals(purchase.getPurchaseType()) && purchase.getHome() != null) {
            dto.setItemName("Home in " + purchase.getHome().getCity());
            dto.setHomeId(purchase.getHome().getId());
        }

        return dto;
    }
}