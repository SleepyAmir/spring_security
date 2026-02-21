package com.mftplus.spring_security.bankAccount.mapper;



import com.mftplus.spring_security.bankAccount.dto.BankAccountDto;
import com.mftplus.spring_security.bankAccount.model.entity.BankAccount;
import org.mapstruct.Mapper;


@Mapper(componentModel="spring")
public interface BankAccountMapper {
    BankAccountDto toDto(BankAccount bankAccount);

    BankAccount toEntity(BankAccountDto bankAccountDto);




}
