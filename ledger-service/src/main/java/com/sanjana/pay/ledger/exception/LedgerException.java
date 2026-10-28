package com.sanjana.pay.ledger.exception;

/**
 * WHAT: the parent of all our business errors.
 * WHY: one parent lets the error handler catch them all in one place.
 *      code is the short name sent to the client.
 */
public abstract class LedgerException extends RuntimeException {

    private final String code;

    protected LedgerException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}