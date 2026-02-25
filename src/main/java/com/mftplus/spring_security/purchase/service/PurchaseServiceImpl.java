package com.mftplus.spring_security.purchase.service;


import com.mftplus.spring_security.bankAccount.model.entity.BankAccount;
import com.mftplus.spring_security.bankAccount.repository.BankAccountRepository;
import com.mftplus.spring_security.core.model.User;
import com.mftplus.spring_security.core.repository.UserRepository;
import com.mftplus.spring_security.product.model.Product;
import com.mftplus.spring_security.product.repository.ProductRepository;
import com.mftplus.spring_security.purchase.dto.PurchaseDto;
import com.mftplus.spring_security.purchase.mapper.PurchaseMapper;
import com.mftplus.spring_security.purchase.model.entity.Purchase;
import com.mftplus.spring_security.purchase.model.enums.PurchaseStatus;
import com.mftplus.spring_security.purchase.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService{

    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    private final PurchaseMapper purchaseMapper;

    @Override
    @Transactional
    public PurchaseDto purchase(Long userId, PurchaseDto request) {
        // پیدا کردن کاربر
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("کاربر یافت نشد"));

        // پیدا کردن محصول
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("محصول یافت نشد"));

        // پیدا کردن حساب بانکی
        BankAccount bankAccount = bankAccountRepository.findById(request.getBankAccountId())
                .orElseThrow(() -> new IllegalArgumentException("حساب بانکی یافت نشد"));

        // چک کردن مالکیت حساب
        if (!bankAccount.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("این حساب متعلق به شما نیست");
        }

        // چک خرید تکراری
        if (purchaseRepository.existsByUserIdAndProductId(userId, request.getProductId())) {
            throw new IllegalArgumentException("شما قبلاً این محصول را خریده‌اید");
        }

        // چک موجودی
        BigDecimal price = BigDecimal.valueOf(product.getPrice());
        if (bankAccount.getBalance().compareTo(price) < 0) {
            throw new IllegalArgumentException("موجودی کافی نیست");
        }

        // کسر مبلغ
        bankAccount.setBalance(bankAccount.getBalance().subtract(price));
        bankAccountRepository.save(bankAccount);

        // ذخیره خرید
        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setProduct(product);
        purchase.setBankAccount(bankAccount);
        purchase.setInvoiceNumber("INV-" + System.currentTimeMillis());
        purchase.setPurchasedAt(LocalDateTime.now());
        purchase.setStatus(PurchaseStatus.COMPLETED);

        Purchase saved = purchaseRepository.save(purchase);
        return purchaseMapper.toDto(saved);
    }

    @Override
    public List<PurchaseDto> findByUserId(Long userId) {
        return purchaseRepository.findByUserIdOrderByPurchasedAtDesc(userId)
                .stream()
                .map(purchaseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PurchaseDto findById(Long purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("خرید یافت نشد"));
        return purchaseMapper.toDto(purchase);
    }
}
