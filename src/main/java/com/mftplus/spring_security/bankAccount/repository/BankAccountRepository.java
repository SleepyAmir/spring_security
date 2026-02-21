package com.mftplus.spring_security.bankAccount.repository;

import com.mftplus.spring_security.bankAccount.model.entity.BankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {


    @Query(
            value = "SELECT * FROM bank_account WHERE deleted = true",
            countQuery = "SELECT count(*) FROM bank_account WHERE deleted = true",
            nativeQuery = true
    )
    Page<BankAccount> findAllDeleted(Pageable pageable);


    @Query(
            value = "SELECT * FROM bank_account",
            countQuery = "SELECT count(*) FROM bank_account",
            nativeQuery = true
    )
    Page<BankAccount> findAllEvenDeleted(Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "UPDATE bank_account SET deleted = false WHERE id = :id", nativeQuery = true)
    void restoreById(@Param("id") Long id);

    Page<BankAccount> findByNameAndFamily(String family, String name, Pageable pageable);

    Page<BankAccount> findByAccountNumber(String accountNumber, Pageable pageable);

    Page<BankAccount> findByNameAndAccountNumber(String name, String accountNumber, Pageable pageable);
}
