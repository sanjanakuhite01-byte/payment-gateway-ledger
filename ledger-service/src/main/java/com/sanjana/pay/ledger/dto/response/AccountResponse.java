package com.sanjana.pay.ledger.dto.response;

import com.sanjana.pay.ledger.enums.AccountType;

import java.time.Instant;

/** WHAT: an account sent back to the caller. */
public record AccountResponse(
        String id,
        String ownerType,
        String ownerRef,
        AccountType accountType,
        String name,
        String currency,
        Instant createdAt
) {
}