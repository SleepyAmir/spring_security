package com.mftplus.spring_security.bankAccount.service;


import com.mftplus.spring_security.bankAccount.dto.BankAccountDto;
import com.mftplus.spring_security.bankAccount.mapper.BankAccountMapper;
import com.mftplus.spring_security.bankAccount.model.entity.BankAccount;
import com.mftplus.spring_security.bankAccount.model.enums.AccountType;
import com.mftplus.spring_security.bankAccount.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final BankAccountMapper bankAccountMapper;
    private final SecureRandom random = new SecureRandom();
    private static final String Account_BIN = "6037";
    private static final BigDecimal Default_Balance = new BigDecimal("1000.00");

    @Transactional
    @Override
    public void save(BankAccountDto bankAccountDto) {
        bankAccountDto.setAccountNumber(generateAccountNumber());
        bankAccountDto.setBalance(Default_Balance);

        BankAccount bankAccount = bankAccountMapper.toEntity(bankAccountDto);
        bankAccountRepository.save(bankAccount);
    }

    @Transactional
    @Override
    public void update(BankAccountDto bankAccountDto) {
        if (bankAccountDto.getId() == null) {
            throw new IllegalArgumentException("ID cannot be null for update");
        }

        BankAccount existingAccount = bankAccountRepository.findById(bankAccountDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        existingAccount.setName(bankAccountDto.getName());
        existingAccount.setFamily(bankAccountDto.getFamily());
        existingAccount.setType(bankAccountDto.getType());
        bankAccountRepository.save(existingAccount);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        bankAccountRepository.deleteById(id);
    }

    @Override
    public BankAccountDto findById(Long id) {
        return bankAccountRepository.findById(id)
                .map(bankAccountMapper::toDto)
                .orElse(null);
    }

    @Override
    public List<BankAccountDto> findAll() {
        return bankAccountRepository.findAll()
                .stream()
                .map(bankAccountMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BankAccountDto> findAll(Pageable pageable) {
        return bankAccountRepository.findAll(pageable)
                .map(bankAccountMapper::toDto);
    }

    @Transactional
    @Override
    public Page<BankAccountDto> findAllDeleted(Pageable pageable) {
        return bankAccountRepository.findAllDeleted(pageable)
                .map(bankAccountMapper::toDto);
    }

    @Override
    public Page<BankAccountDto> findAllEvenDeleted(Pageable pageable) {
        return bankAccountRepository.findAllEvenDeleted(pageable)
                .map(bankAccountMapper::toDto);
    }

    @Transactional
    @Override
    public void restoreById(Long id) {
        bankAccountRepository.restoreById(id);
    }

    @Override
    public Page<BankAccountDto> findByNameAndFamily(String name, String family, Pageable pageable) {
        return bankAccountRepository.findByNameAndFamily(name, family, pageable)
                .map(bankAccountMapper::toDto);
    }

    @Override
    public Page<BankAccountDto> findByAccountNumber(String accountNumber, Pageable pageable) {
        return bankAccountRepository.findByAccountNumber(accountNumber,pageable)
                .map(bankAccountMapper::toDto);
    }

    @Override
    public Page<BankAccountDto> findByNameAndAccountNumber(String name, String accountNumber, Pageable pageable) {
        return bankAccountRepository.findByNameAndAccountNumber(name,accountNumber,pageable)
                .map(bankAccountMapper::toDto);
    }

    @Override
    public BankAccountDto issueAccount(Long id, AccountType accountType, BankAccountDto bankAccountDto) {
        return null;
    }

    private String generateAccountNumber() {
        StringBuilder sb = new StringBuilder(Account_BIN);
        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

}