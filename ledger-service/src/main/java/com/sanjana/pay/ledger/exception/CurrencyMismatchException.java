package com.sanjana.pay.ledger.exception;

/**
 * WHAT: a line uses an account with a different currency than the transaction.
 * WHY: mixing INR and USD in one transaction would break the books. (Will become HTTP 422.)
 */
public class CurrencyMismatchException extends LedgerException {

    public CurrencyMismatchException(String accountId, String accountCurrency, String txCurrency) {
        super("CURRENCY_MISMATCH", "Account " + accountId + " is " + accountCurrency
                + " but the transaction is " + txCurrency);
    }
}