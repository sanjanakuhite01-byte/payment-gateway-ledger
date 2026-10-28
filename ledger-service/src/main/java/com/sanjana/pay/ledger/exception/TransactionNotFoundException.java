package com.sanjana.pay.ledger.exception;

/** WHAT: we were asked to reverse a transaction that does not exist. (Will become HTTP 404.) */
public class TransactionNotFoundException extends LedgerException {

    public TransactionNotFoundException(String transactionId) {
        super("TRANSACTION_NOT_FOUND", "Transaction not found: " + transactionId);
    }
}