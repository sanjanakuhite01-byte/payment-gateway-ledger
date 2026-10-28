package com.sanjana.pay.ledger.dto.response;

import java.time.Instant;
import java.util.List;

/**
 * WHAT: a posted transaction with all its lines.
 * WHY: this is the receipt the caller gets after posting.
 */
public record TransactionResponse(
        String id,
        String postingKey,
        String type,
        String reference,
        String description,
        String currency,
        String reversesId,
        Instant createdAt,
        List<EntryLineResponse> entries
) {
}