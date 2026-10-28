package com.sanjana.pay.ledger.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

/**
 * WHAT: one row of "ledger_transactions": the envelope of one money movement.
 * WHY: its lines (entries) live inside it. The posting_key makes posting safe
 *      to repeat (idempotency): the same key can never create a second transaction.
 *      No setters: a posted transaction is never changed (append-only ledger).
 */
@Entity
@Table(name = "ledger_transactions")
public class LedgerTransaction {

    @Id
    @Column(name = "id", length = 40)
    private String id;

    @Column(name = "posting_key", nullable = false, length = 100, updatable = false)
    private String postingKey;

    // SALE, REFUND, SETTLEMENT, PAYOUT, ADJUSTMENT, ...
    @Column(name = "type", nullable = false, length = 30)
    private String type;

    // The business reference, for example the payment id
    @Column(name = "reference", length = 60)
    private String reference;

    @Column(name = "description", length = 200)
    private String description;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    // Set only when this transaction cancels another one
    @Column(name = "reverses_id", length = 40)
    private String reversesId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected LedgerTransaction() {
    }

    public LedgerTransaction(String id, String postingKey, String type, String reference,
                             String description, String currency, String reversesId) {
        this.id = id;
        this.postingKey = postingKey;
        this.type = type;
        this.reference = reference;
        this.description = description;
        this.currency = currency;
        this.reversesId = reversesId;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public String getPostingKey() { return postingKey; }
    public String getType() { return type; }
    public String getReference() { return reference; }
    public String getDescription() { return description; }
    public String getCurrency() { return currency; }
    public String getReversesId() { return reversesId; }
    public Instant getCreatedAt() { return createdAt; }
}
