package com.sanjana.pay.ledger.dto.request;

import com.sanjana.pay.ledger.enums.Direction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * WHAT: one line of a transaction sent by the caller.
 * WHY: amount is in paise and must be above zero. The direction says the side.
 */
public record EntryLineRequest(
        @NotBlank @Size(max = 40) String accountId,
        @NotNull Direction direction,
        @Positive long amount
) {
}