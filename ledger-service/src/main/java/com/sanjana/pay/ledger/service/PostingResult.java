package com.sanjana.pay.ledger.service;

import com.sanjana.pay.ledger.dto.response.TransactionResponse;

/**
 * WHAT: what PostingService returns.
 * WHY: replayed = true means "this key was already posted, here is the old result".
 *      The controller will answer 201 for new and 200 for replayed.
 */
public record PostingResult(TransactionResponse transaction, boolean replayed) {
}