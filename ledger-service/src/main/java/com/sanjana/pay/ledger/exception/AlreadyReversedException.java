package com.sanjana.pay.ledger.exception;

/**
 * WHAT: this transaction was already reversed once. (Will become HTTP 409.)
 * WHY: a refund must not cancel the same sale twice.
 */
public class AlreadyReversedException extends LedgerException {

    public AlreadyReversedException(String transactionId) {
        super("ALREADY_REVERSED", "Transaction was already reversed: " + transactionId);
    }
}