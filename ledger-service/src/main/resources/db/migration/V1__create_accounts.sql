-- WHAT: the accounts table of the ledger.
-- WHY: money is never "a number on a payment". It always sits in an account,
--      and every ledger entry points to one account.
-- NOTE: Flyway runs this file once. Never edit it after it has run.
--       To change the table later, add a new file (V2, V3, ...).

CREATE TABLE accounts (
    -- Id with a prefix, like acc_8f3k2... (never a plain counter)
                          id            VARCHAR(40)  PRIMARY KEY,

    -- Who owns it: PLATFORM (us), MERCHANT, or BANK
                          owner_type    VARCHAR(20)  NOT NULL,

    -- The merchant id, for example mer_123. Empty for platform accounts.
                          owner_ref     VARCHAR(40),

    -- Accounting type. Decides how the balance is read.
    --   ASSET     = money we hold (bank clearing)
    --   LIABILITY = money we owe (merchant payable)
    --   REVENUE   = money we earn (our fees)
    --   EXPENSE   = money we spend
                          account_type  VARCHAR(20)  NOT NULL
                              CHECK (account_type IN ('ASSET', 'LIABILITY', 'REVENUE', 'EXPENSE')),

                          name          VARCHAR(100) NOT NULL,

    -- One currency per account (INR for now)
                          currency      CHAR(3)      NOT NULL,

                          created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),

    -- The same owner cannot have two accounts with the same name and currency
                          CONSTRAINT uq_account UNIQUE (owner_type, owner_ref, name, currency)
);