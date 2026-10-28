-- WHAT: the transaction header table and the entry (line) table.
-- WHY: one money movement = one transaction, made of 2 or more entries.
--      Debit total must equal credit total (the rule is enforced in a later file).
-- NOTE: never edit this file after it has run. Add a new V file to change anything.

CREATE TABLE ledger_transactions (
    -- Id with a prefix, like led_9d2k... (never a plain counter)
                                     id            VARCHAR(40)  PRIMARY KEY,

    -- The key that makes posting safe to repeat (idempotency).
    -- The same key can never create a second transaction.
                                     posting_key   VARCHAR(100) NOT NULL UNIQUE,

    -- What kind of movement: SALE, REFUND, SETTLEMENT, PAYOUT, ADJUSTMENT, ...
                                     type          VARCHAR(30)  NOT NULL,

    -- The business reference, for example the payment id (pay_123)
                                     reference     VARCHAR(60),

                                     description   VARCHAR(200),

    -- One currency per transaction (INR for now)
                                     currency      CHAR(3)      NOT NULL,

    -- If this transaction cancels another one, this points to the original.
    -- UNIQUE means a transaction can be reversed only once.
                                     reverses_id   VARCHAR(40)  UNIQUE REFERENCES ledger_transactions (id),

                                     created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE ledger_entries (
    -- A counter is fine here: entries are only read through their transaction.
                                id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

                                transaction_id  VARCHAR(40)  NOT NULL REFERENCES ledger_transactions (id),
                                account_id      VARCHAR(40)  NOT NULL REFERENCES accounts (id),

    -- DEBIT or CREDIT, nothing else
                                direction       VARCHAR(6)   NOT NULL CHECK (direction IN ('DEBIT', 'CREDIT')),

    -- Money in whole paise (Rs 1 = 100 paise). Never decimals.
    -- Must be above zero. The direction says if it goes up or down.
                                amount          BIGINT       NOT NULL CHECK (amount > 0),

                                created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- Indexes: make "show all lines of a transaction" and "show an account's history" fast
CREATE INDEX idx_entries_transaction ON ledger_entries (transaction_id);
CREATE INDEX idx_entries_account     ON ledger_entries (account_id, created_at);