package com.sanjana.pay.ledger.exception;

/** WHAT: a line uses an account that does not exist. (Will become HTTP 404.) */
public class AccountNotFoundException extends LedgerException {

    public AccountNotFoundException(String accountId) {
        super("ACCOUNT_NOT_FOUND", "Account not found: " + accountId);
    }
}