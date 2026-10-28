package com.sanjana.pay.ledger.repository;

import com.sanjana.pay.ledger.entity.LedgerEntry;
import com.sanjana.pay.ledger.enums.Direction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * WHAT: database access for ledger entries (the lines).
 * WHY: a balance is never stored. We always compute it from the entries,
 *      so it can never be out of date or wrong.
 */
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {

    List<LedgerEntry> findByTransactionId(String transactionId);

    // Total paise for one account on one side (DEBIT or CREDIT).
    // coalesce(..., 0) returns 0 when the account has no entries yet.
    @Query("""
           select coalesce(sum(e.amount), 0)
           from LedgerEntry e
           where e.accountId = :accountId and e.direction = :direction
           """)
    long sumAmount(@Param("accountId") String accountId,
                   @Param("direction") Direction direction);
}