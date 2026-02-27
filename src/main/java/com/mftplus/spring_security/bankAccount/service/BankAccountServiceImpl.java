package com.mftplus.spring_security.bankAccount.service;

import com.mftplus.spring_security.bankAccount.dto.BankAccountDto;
import com.mftplus.spring_security.bankAccount.mapper.BankAccountMapper;
import com.mftplus.spring_security.bankAccount.model.entity.BankAccount;
import com.mftplus.spring_security.bankAccount.model.enums.AccountType;
import com.mftplus.spring_security.bankAccount.repository.BankAccountRepository;
import com.mftplus.spring_security.core.model.User;
import com.mftplus.spring_security.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final BankAccountMapper bankAccountMapper;
    private final UserRepository userRepository;

    private final SecureRandom random = new SecureRandom();
    private static final String ACCOUNT_BIN = "6037";
    private static final BigDecimal DEFAULT_BALANCE = new BigDecimal("1000.00");

    @Transactional
    @Override
    public void save(BankAccountDto bankAccountDto, Long userId) {
        log.info("Creating bank account for user: {}", userId);

        User user = findUser(userId);
        bankAccountDto.setAccountNumber(generateAccountNumber());
        bankAccountDto.setBalance(DEFAULT_BALANCE);

        BankAccount bankAccount = bankAccountMapper.toEntity(bankAccountDto);
        bankAccount.setUser(user);

        bankAccountRepository.save(bankAccount);
        log.info("Bank account created successfully with account number: {}", bankAccount.getAccountNumber());
    }

    @Transactional
    @Override
    public void update(BankAccountDto bankAccountDto) {
        log.info("Updating bank account with id: {}", bankAccountDto.getId());

        if (bankAccountDto.getId() == null) {
            throw new IllegalArgumentException("ID cannot be null for update");
        }

        BankAccount existingAccount = bankAccountRepository.findById(bankAccountDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + bankAccountDto.getId()));

        existingAccount.setType(bankAccountDto.getType());
        bankAccountRepository.save(existingAccount);
        log.info("Bank account updated successfully");
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        log.info("Deleting bank account with id: {}", id);

        if (!bankAccountRepository.existsById(id)) {
            throw new IllegalArgumentException("Bank account not found with id: " + id);
        }

        bankAccountRepository.deleteById(id);
        log.info("Bank account deleted successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public BankAccountDto findById(Long id) {
        log.debug("Finding bank account by id: {}", id);
        return bankAccountRepository.findById(id)
                .map(bankAccountMapper::toDto)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankAccountDto> findAll() {
        log.debug("Finding all bank accounts");
        return bankAccountRepository.findAll()
                .stream()
                .map(bankAccountMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BankAccountDto> findAll(Pageable pageable) {
        log.debug("Finding all bank accounts with pagination");
        return bankAccountRepository.findAll(pageable)
                .map(bankAccountMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankAccountDto> findByUserId(Long userId) {
        log.debug("Finding bank accounts for user: {}", userId);
        return bankAccountRepository.findByUserId(userId)
                .stream()
                .map(bankAccountMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BankAccountDto> findByUserId(Long userId, Pageable pageable) {
        log.debug("Finding bank accounts for user: {} with pagination", userId);
        return bankAccountRepository.findByUserId(userId, pageable)
                .map(bankAccountMapper::toDto);
    }

    @Transactional
    @Override
    public Page<BankAccountDto> findAllDeleted(Pageable pageable) {
        log.debug("Finding all deleted bank accounts");
        return bankAccountRepository.findAllDeleted(pageable)
                .map(bankAccountMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BankAccountDto> findAllEvenDeleted(Pageable pageable) {
        log.debug("Finding all bank accounts including deleted");
        return bankAccountRepository.findAllEvenDeleted(pageable)
                .map(bankAccountMapper::toDto);
    }

    @Transactional
    @Override
    public void restoreById(Long id) {
        log.info("Restoring bank account with id: {}", id);
        bankAccountRepository.restoreById(id);
        log.info("Bank account restored successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BankAccountDto> findByAccountNumber(String accountNumber, Pageable pageable) {
        log.debug("Finding bank account by account number: {}", accountNumber);
        return bankAccountRepository.findByAccountNumber(accountNumber, pageable)
                .map(bankAccountMapper::toDto);
    }

    @Override
    @Transactional
    public BankAccountDto issueAccount(Long userId, AccountType accountType) {
        log.info("Issuing new {} account for user: {}", accountType, userId);

        User user = findUser(userId);

        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber(generateAccountNumber());
        bankAccount.setBalance(DEFAULT_BALANCE);
        bankAccount.setType(accountType);
        bankAccount.setUser(user);

        BankAccount saved = bankAccountRepository.save(bankAccount);
        log.info("Account issued successfully with account number: {}", saved.getAccountNumber());

        return bankAccountMapper.toDto(saved);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private Helper Methods
    // ─────────────────────────────────────────────────────────────────────────

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
    }

    private String generateAccountNumber() {
        StringBuilder sb = new StringBuilder(ACCOUNT_BIN);
        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}