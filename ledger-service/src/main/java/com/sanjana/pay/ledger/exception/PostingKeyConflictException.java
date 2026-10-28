package com.sanjana.pay.ledger.exception;

/**
 * WHAT: the same posting key was sent again, but with a different body.
 * WHY: same key + same body = safe repeat (we return the old result).
 *      Same key + different body = a bug on the caller's side. We refuse. (Will become HTTP 409.)
 */
public class PostingKeyConflictException extends LedgerException {

    public PostingKeyConflictException(String postingKey) {
        super("POSTING_KEY_CONFLICT", "Posting key already used with different data: " + postingKey);
    }
}