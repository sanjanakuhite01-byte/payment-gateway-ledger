package com.sanjana.pay.ledger.dto.response;

import com.sanjana.pay.ledger.enums.Direction;

/** WHAT: one line of a posted transaction, sent back to the caller. */
public record EntryLineResponse(
        String accountId,
        Direction direction,
        long amount
) {
}