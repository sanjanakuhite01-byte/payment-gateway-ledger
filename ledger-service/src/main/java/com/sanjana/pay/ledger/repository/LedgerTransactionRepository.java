package com.sanjana.pay.ledger.repository;

import com.sanjana.pay.ledger.entity.LedgerTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * WHAT: database access for ledger transactions.
 * WHY: findByPostingKey keeps us idempotent. If the key already exists,
 *      we return the old transaction instead of posting money twice.
 *      existsByReversesId stops us from reversing the same transaction twice.
 */
public interface LedgerTransactionRepository extends JpaRepository<LedgerTransaction, String> {

    Optional<LedgerTransaction> findByPostingKey(String postingKey);

    boolean existsByReversesId(String reversesId);
}