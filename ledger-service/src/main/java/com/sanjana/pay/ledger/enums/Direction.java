package com.sanjana.pay.ledger.enums;

/**
 * WHAT: which side of the books an entry is on.
 * WHY: every entry is a DEBIT or a CREDIT. In one transaction,
 *      total debit must equal total credit.
 */
public enum Direction {
    DEBIT, CREDIT
}