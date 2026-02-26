package com.mftplus.spring_security.bankAccount.dto;

import com.mftplus.spring_security.bankAccount.model.enums.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BankAccountDto {

    private Long id;

    private String accountNumber;

    private BigDecimal balance;

    @NotNull(message = "Account type is required")
    private AccountType type;

    // ⚠️ فیلدهای جدید مربوط به User
    private Long userId;

    private String username; // نام کاربری

    private String userFullName; // نام کامل (از Person)
}