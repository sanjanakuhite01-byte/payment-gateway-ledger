package com.sanjana.pay.ledger.entity;

import com.sanjana.pay.ledger.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

/**
 * WHAT: one row of the "accounts" table.
 * WHY: money always sits in an account, never "on a payment".
 *      No setters on purpose, so an account cannot be changed by accident.
 */
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @Column(name = "id", length = 40)
    private String id;

    @Column(name = "owner_type", nullable = false, length = 20)
    private String ownerType;

    @Column(name = "owner_ref", length = 40)
    private String ownerRef;

    // STRING = save the word (ASSET), not a number. Safe if the enum order changes.
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 20)
    private AccountType accountType;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // The table column is CHAR(3). This tells Hibernate, so its startup check passes.
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // JPA needs an empty constructor. Protected = only JPA may use it.
    protected Account() {
    }

    public Account(String id, String ownerType, String ownerRef,
                   AccountType accountType, String name, String currency) {
        this.id = id;
        this.ownerType = ownerType;
        this.ownerRef = ownerRef;
        this.accountType = accountType;
        this.name = name;
        this.currency = currency;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public String getOwnerType() { return ownerType; }
    public String getOwnerRef() { return ownerRef; }
    public AccountType getAccountType() { return accountType; }
    public String getName() { return name; }
    public String getCurrency() { return currency; }
    public Instant getCreatedAt() { return createdAt; }
}