package com.mftplus.spring_security.bankAccount.mapper;

import com.mftplus.spring_security.bankAccount.dto.BankAccountDto;
import com.mftplus.spring_security.bankAccount.model.entity.BankAccount;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BankAccountMapper {

    // ⚠️ mapping اضافه شد
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "userFullName", ignore = true) // پر می‌شود در service
    BankAccountDto toDto(BankAccount bankAccount);

    // ⚠️ mapping اضافه شد
    @Mapping(target = "user", ignore = true) // Set می‌شود در service
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    BankAccount toEntity(BankAccountDto bankAccountDto);
}