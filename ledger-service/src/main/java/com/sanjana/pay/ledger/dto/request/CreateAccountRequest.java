package com.sanjana.pay.ledger.dto.request;

import com.sanjana.pay.ledger.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * WHAT: the body of "create an account".
 * WHY: ownerType says who owns it (for example MERCHANT or PLATFORM) and ownerRef is
 *      that owner's id. The id of the account is made by the server, not the caller.
 */
public record CreateAccountRequest(
        @NotBlank @Size(max = 20) String ownerType,
        @Size(max = 40) String ownerRef,
        @NotNull AccountType accountType,
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Pattern(regexp = "[A-Z]{3}", message = "must be a 3-letter code like INR") String currency
) {
}