package com.mftplus.spring_security.purchase.service;

import com.mftplus.spring_security.purchase.dto.PurchaseDto;

import java.util.List;

public interface PurchaseService {

    PurchaseDto purchase(Long userId, PurchaseDto request);

    List<PurchaseDto> findByUserId(Long userId);

    PurchaseDto findById(Long purchaseId);


}
