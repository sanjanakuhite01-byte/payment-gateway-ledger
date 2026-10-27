-- WHAT: runs once, the first time the Postgres container starts.
-- WHY: each service owns its own database. The ledger database holds money
--      records, and no other service may touch it directly.
CREATE DATABASE ledger_db;
CREATE DATABASE payment_db;