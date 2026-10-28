package com.sanjana.pay.ledger.enums;

/**
 * WHAT: the four accounting types of an account.
 * WHY: the type decides how an account's balance is read.
 *      ASSET = money we hold, LIABILITY = money we owe,
 *      REVENUE = money we earn, EXPENSE = money we spend.
 *      The names must match the CHECK rule in the accounts table.
 */
public enum AccountType {
    ASSET, LIABILITY, REVENUE, EXPENSE
}