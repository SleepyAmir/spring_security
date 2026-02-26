package com.mftplus.spring_security.bankAccount.service;

import com.mftplus.spring_security.bankAccount.dto.BankAccountDto;
import com.mftplus.spring_security.bankAccount.model.enums.AccountType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BankAccountService {

    void save(BankAccountDto bankAccountDto, Long userId); // ⚠️ اضافه شد userId

    void update(BankAccountDto bankAccountDto);

    void deleteById(Long id);

    BankAccountDto findById(Long id);

    List<BankAccountDto> findAll();

    Page<BankAccountDto> findAll(Pageable pageable);

    // ⚠️ متدهای جدید
    List<BankAccountDto> findByUserId(Long userId);

    Page<BankAccountDto> findByUserId(Long userId, Pageable pageable);

    Page<BankAccountDto> findAllDeleted(Pageable pageable);

    Page<BankAccountDto> findAllEvenDeleted(Pageable pageable);

    void restoreById(Long id);

    Page<BankAccountDto> findByAccountNumber(String accountNumber, Pageable pageable);

    BankAccountDto issueAccount(Long userId, AccountType accountType);

    // ⚠️ متدهای قدیمی حذف شدند:
    // findByNameAndFamily ❌
    // findByNameAndAccountNumber ❌
}