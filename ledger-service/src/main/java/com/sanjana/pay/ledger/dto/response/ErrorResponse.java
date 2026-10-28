package com.sanjana.pay.ledger.dto.response;

import java.time.Instant;
import java.util.List;

/**
 * WHAT: the one error shape used by the whole API (project rule: common error format).
 * WHY: clients always get the same fields, so they can handle errors in one place.
 *      code = short name for machines (for example UNBALANCED), message = text for humans.
 */
public record ErrorResponse(
        String code,
        String message,
        Instant timestamp,
        List<String> details
) {
}