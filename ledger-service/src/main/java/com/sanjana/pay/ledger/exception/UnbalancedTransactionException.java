package com.sanjana.pay.ledger.exception;

/**
 * WHAT: total debit is not equal to total credit. (Will become HTTP 422.)
 * WHY: we check this in Java first for a clear message.
 *      The database trigger stays as the last safety net.
 */
public class UnbalancedTransactionException extends LedgerException {

    public UnbalancedTransactionException(long debit, long credit) {
        super("UNBALANCED", "Transaction is not balanced: debit " + debit + ", credit " + credit);
    }
}