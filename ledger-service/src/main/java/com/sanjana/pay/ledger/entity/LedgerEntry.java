package com.sanjana.pay.ledger.entity;

import com.sanjana.pay.ledger.enums.Direction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * WHAT: one row of "ledger_entries": one line of a transaction.
 * WHY: a line says "this account goes up or down by this amount".
 *      amount is whole paise (Rs 1 = 100 paise), never a decimal,
 *      so there are no rounding errors. It is always above zero;
 *      the direction says which way the money goes. No setters.
 */
@Entity
@Table(name = "ledger_entries")
public class LedgerEntry {

    // The database makes this number itself (identity column)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "transaction_id", nullable = false, length = 40, updatable = false)
    private String transactionId;

    @Column(name = "account_id", nullable = false, length = 40, updatable = false)
    private String accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false, length = 6, updatable = false)
    private Direction direction;

    @Column(name = "amount", nullable = false, updatable = false)
    private long amount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected LedgerEntry() {
    }

    public LedgerEntry(String transactionId, String accountId, Direction direction, long amount) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.direction = direction;
        this.amount = amount;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getTransactionId() { return transactionId; }
    public String getAccountId() { return accountId; }
    public Direction getDirection() { return direction; }
    public long getAmount() { return amount; }
    public Instant getCreatedAt() { return createdAt; }
}