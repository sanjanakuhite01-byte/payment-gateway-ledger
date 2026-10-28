package com.sanjana.pay.ledger.dto.response;
/**
 * WHAT: the balance of one account, in paise.
 * WHY: we show debit total and credit total too, so anyone can check the math.
 *      balance is the natural balance: debit - credit for ASSET and EXPENSE,
 *      credit - debit for LIABILITY and REVENUE. A positive number means the
 *      account holds that much in its normal direction.
 */
public record BalanceResponse(
        String accountId,
        String currency,
        long debitTotal,
        long creditTotal,
        long balance
) {
}