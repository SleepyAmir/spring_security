package com.mftplus.spring_security.bankAccount.dto;

import com.mftplus.spring_security.bankAccount.model.enums.AccountType;
import jakarta.validation.constraints.Pattern;
import lombok.Data;



@Data
public class BankAccountDto {

    private Long id;

    @Pattern(regexp = "^[a-zA-Z\\s]{3,20}$", message = "Invalid Name")
    private String name;

    @Pattern(regexp = "^[a-zA-Z\\s]{3,20}$", message = "Invalid Name")
    private String family;

    private String accountNumber;


    private Double balance;


    private AccountType type;


}
