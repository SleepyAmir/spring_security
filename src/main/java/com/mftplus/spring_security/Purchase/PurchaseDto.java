package com.mftplus.spring_security.Purchase;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDto {

    private Long id;
    private String invoiceNumber;
    private LocalDateTime purchasedAt;
    private Double price;
    private String purchaseType; // "PRODUCT" یا "HOME"

    private Long userId;
    private String username;

    private Long bankAccountId;
    private String accountNumber;

    // ⚠️ نام محصول یا خانه
    private String itemName;

    // ⚠️ اطلاعات اختیاری
    private Long productId;
    private Long homeId;
}