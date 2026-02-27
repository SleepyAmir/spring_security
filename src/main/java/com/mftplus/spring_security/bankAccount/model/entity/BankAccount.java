package com.mftplus.spring_security.bankAccount.model.entity;

import com.mftplus.spring_security.bankAccount.model.enums.AccountType;
import com.mftplus.spring_security.core.model.Person;
import com.mftplus.spring_security.core.model.User;
import com.mftplus.spring_security.product.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;


@Entity(name="BankAccountEntity")
@Table(name="bank_account")
@SQLDelete(sql = "UPDATE bank_account SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BankAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true, length = 16)
    private String accountNumber;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


}
