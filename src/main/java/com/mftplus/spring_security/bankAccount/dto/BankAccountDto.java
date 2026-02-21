package com.mftplus.spring_security.bankAccount.dto;

import com.mftplus.spring_security.bankAccount.model.enums.AccountType;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BankAccountDto {

    private Long id;

    @Pattern(regexp = "^[a-zA-Z\\s]{3,20}$", message = "Invalid Name")
    private String name;

    @Pattern(regexp = "^[a-zA-Z\\s]{3,20}$", message = "Invalid Name")
    private String family;

    private String accountNumber;


    private BigDecimal balance;


    private AccountType type;


}
