package com.mftplus.spring_security.purchase.dto;



import com.mftplus.spring_security.purchase.model.enums.PurchaseStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PurchaseDto {
    private Long id;
    private Long userId;
    private String username;
    private Long productId;
    private String productName;
    private Double productPrice;   // قیمت فعلی کالا (از product)
    private Long bankAccountId;
    private String accountNumber;
    private String invoiceNumber;
    private LocalDateTime purchasedAt;
    private PurchaseStatus status;
}