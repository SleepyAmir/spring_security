package com.mftplus.spring_security.bankAccount.mapper;

import com.mftplus.spring_security.bankAccount.dto.BankAccountDto;
import com.mftplus.spring_security.bankAccount.model.entity.BankAccount;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BankAccountMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "userFullName", source = "user.fullName")
    BankAccountDto toDto(BankAccount bankAccount);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    BankAccount toEntity(BankAccountDto bankAccountDto);
}