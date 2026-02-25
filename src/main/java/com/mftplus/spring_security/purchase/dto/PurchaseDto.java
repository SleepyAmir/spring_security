package com.mftplus.spring_security.purchase.dto;



import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PurchaseDto {
    @NotNull(message = "شناسه کالا الزامی است")
    private Long productId;

    @NotNull(message = "شناسه حساب بانکی الزامی است")
    private Long bankAccountId;
}