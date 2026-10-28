package com.sanjana.pay.ledger.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * WHAT: the body of "post a transaction".
 * WHY: postingKey is the idempotency key. Sending the same key again never
 *      posts money twice. At least 2 lines are needed, because one line
 *      alone can never be balanced.
 */
public record PostTransactionRequest(
        @NotBlank @Size(max = 100) String postingKey,
        @NotBlank @Size(max = 30) String type,
        @Size(max = 60) String reference,
        @Size(max = 200) String description,
        @NotBlank @Pattern(regexp = "[A-Z]{3}", message = "must be a 3-letter code like INR") String currency,
        @Size(max = 40) String reversesId,
        @NotNull @Size(min = 2, max = 50) @Valid List<EntryLineRequest> entries
) {
}