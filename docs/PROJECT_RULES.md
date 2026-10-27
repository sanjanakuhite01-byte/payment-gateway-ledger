# Project Rules

## Money
1. Money is a whole number in paise (Rs 1 = 100 paise) plus a currency. Never use decimals or double.
2. Every amount must be above zero.

## Ledger (the books)
3. Every transaction has at least 2 lines. Total debit must equal total credit.
4. Ledger entries are never updated or deleted. To fix a mistake, post a reversing transaction.
5. Only the ledger-service writes money tables.

## Safety
6. Every money POST needs an Idempotency-Key. Same key means same answer, never a second payment.
7. Never call another service (bank, ledger) inside a database transaction.
8. Never retry a money call blindly. If the result is unknown, ask the bank for the status.

## Code
9. Tables are changed only by Flyway migration files. Never edit the database by hand.
10. Layers: api (web), application (use cases), domain (rules), infrastructure (database, clients).
11. Use constructor injection. Do not put database entities in API responses.
12. Every error uses the same error format.

## Security
13. Never log passwords, secrets, tokens or signatures.
14. Secrets come from environment variables, never from code or Git.
15. Merchant A must never see merchant B's data.