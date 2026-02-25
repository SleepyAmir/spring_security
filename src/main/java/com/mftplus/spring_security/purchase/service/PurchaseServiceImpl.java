package com.mftplus.spring_security.purchase.service;


import com.mftplus.spring_security.bankAccount.repository.BankAccountRepository;
import com.mftplus.spring_security.core.repository.UserRepository;
import com.mftplus.spring_security.product.repository.ProductRepository;
import com.mftplus.spring_security.purchase.dto.PurchaseDto;
import com.mftplus.spring_security.purchase.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService{

    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;




    @Override
    public PurchaseDto purchase(Long userId, PurchaseDto request) {
        return null;
    }

    @Override
    public List<PurchaseDto> findByUserId(Long userId) {
        return List.of();
    }

    @Override
    public PurchaseDto findById(Long purchaseId) {
        return null;
    }
}
