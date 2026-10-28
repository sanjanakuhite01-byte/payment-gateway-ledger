-- WHAT: safety rules that live inside the database.
-- WHY: even if the Java code has a bug, or someone runs SQL by hand, the
--      books must stay correct. The database refuses bad data by itself.
-- NOTE: never edit this file after it has run. Add a new V file instead.

-- =====================================================================
-- RULE 1: every transaction must balance (debit total = credit total)
-- =====================================================================
-- This function is checked at COMMIT time, not at each insert. That is
-- important: lines are inserted one by one, so the total is only correct
-- after the last line is in.
CREATE OR REPLACE FUNCTION check_transaction_balanced() RETURNS trigger AS $$
DECLARE
debit_total  BIGINT;
    credit_total BIGINT;
    line_count   INT;
BEGIN
SELECT COALESCE(SUM(amount) FILTER (WHERE direction = 'DEBIT'), 0),
       COALESCE(SUM(amount) FILTER (WHERE direction = 'CREDIT'), 0),
       COUNT(*)
INTO debit_total, credit_total, line_count
FROM ledger_entries
WHERE transaction_id = NEW.transaction_id;

IF line_count < 2 THEN
        RAISE EXCEPTION 'Transaction % needs at least 2 entries', NEW.transaction_id;
END IF;

    IF debit_total <> credit_total THEN
        RAISE EXCEPTION 'Transaction % is not balanced: debit % credit %',
            NEW.transaction_id, debit_total, credit_total;
END IF;

RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- DEFERRABLE INITIALLY DEFERRED = run the check at commit, after all lines are in.
CREATE CONSTRAINT TRIGGER trg_entries_balanced
    AFTER INSERT ON ledger_entries
    DEFERRABLE INITIALLY DEFERRED
    FOR EACH ROW
    EXECUTE FUNCTION check_transaction_balanced();

-- =====================================================================
-- RULE 2: a transaction header cannot exist without lines
-- =====================================================================
CREATE OR REPLACE FUNCTION check_transaction_has_entries() RETURNS trigger AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM ledger_entries WHERE transaction_id = NEW.id) THEN
        RAISE EXCEPTION 'Transaction % has no entries', NEW.id;
END IF;
RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER trg_transaction_has_entries
    AFTER INSERT ON ledger_transactions
    DEFERRABLE INITIALLY DEFERRED
    FOR EACH ROW
    EXECUTE FUNCTION check_transaction_has_entries();

-- =====================================================================
-- RULE 3: ledger rows can never be changed or deleted (append-only)
-- =====================================================================
-- To fix a mistake, post a new reversing transaction. History stays intact.
CREATE OR REPLACE FUNCTION forbid_ledger_change() RETURNS trigger AS $$
BEGIN
    RAISE EXCEPTION 'Ledger is append-only: % on % is not allowed',
        TG_OP, TG_TABLE_NAME;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_entries_immutable
    BEFORE UPDATE OR DELETE ON ledger_entries
    FOR EACH ROW EXECUTE FUNCTION forbid_ledger_change();

CREATE TRIGGER trg_transactions_immutable
    BEFORE UPDATE OR DELETE ON ledger_transactions
    FOR EACH ROW EXECUTE FUNCTION forbid_ledger_change();

-- TRUNCATE (empty the whole table) skips row triggers, so block it separately
CREATE TRIGGER trg_entries_no_truncate
    BEFORE TRUNCATE ON ledger_entries
    FOR EACH STATEMENT EXECUTE FUNCTION forbid_ledger_change();

CREATE TRIGGER trg_transactions_no_truncate
    BEFORE TRUNCATE ON ledger_transactions
    FOR EACH STATEMENT EXECUTE FUNCTION forbid_ledger_change();