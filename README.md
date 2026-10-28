# Payment Gateway + Double-Entry Ledger

A Razorpay/Stripe-style payment platform built with **Java 21**, **Spring Boot 3** and a
microservice design. At its core is a **double-entry ledger**: a safe accounting book that
records every rupee that moves, so money is never lost, never counted twice, and always
explainable.

> **Status:** work in progress. The ledger service is working. The rest is planned and
> marked clearly below.

## Modules
- common: shared classes (money, error codes)
- ledger-service: the books (debit equals credit, never edited)
- payment-service: merchant API and payment flow
- bank-simulator: fake bank for failure testing
- 
---

## What problem it solves

When a customer pays a merchant online, many things must go right: the bank must confirm,
the merchant must be told, the platform must take its fee, and the books must always match.
Retries, crashes and duplicate requests must never cause a wrong balance. This project
builds that system step by step.

## How a payment flows (target design)

```
Customer pays
     |
Merchant API (signed request + Idempotency-Key)
     |
Payment service -> Fraud check -> Bank simulator (authorize / capture)
     |
Ledger service  (balanced debit + credit entries, append-only)
     |
Outbox table -> Kafka -> Webhooks to the merchant
     |
Settlement (pay merchants) -> Reconciliation (match bank file vs ledger)
```

## Services

| Service | Job | Status |
|---|---|---|
| `common` | Shared code | Done |
| `ledger-service` | Accounts, transactions, entries, balances | **Done** (tests in progress) |
| `payment-service` | Merchant API, payment states, orchestration | Planned |
| `bank-simulator` | Fake bank for authorize, capture, refund, failures | Planned |
| `api-gateway` | Single entry point, JWT login, rate limits | Planned |
| `fraud-service` | Risk rules before a payment is accepted | Planned |
| `webhook-service` | Reliable, signed events sent to merchants (with retries) | Planned |
| `settlement-service` | Daily batch: pay merchants, reconcile with the bank | Planned |
| Frontend dashboard | Merchant view of payments, refunds and settlements | Planned |

## Features

**Ledger (done)**
- Double-entry bookkeeping: every transaction has debit total = credit total
- Append-only ledger: database triggers block UPDATE, DELETE and TRUNCATE
- Unbalanced transactions rejected in Java and again by a database trigger at commit
- Idempotent posting: the same posting key never creates a second transaction
- Concurrent same-key requests handled safely (one wins, the other gets the same result)
- Money in whole paise (BIGINT), no rounding errors
- Balances calculated from entries (never stored), using each account type's natural side
- Same-currency check, account checks, and one clear error format for every failure
- Flyway migrations, schema validation, environment-based secrets

**Payments and platform (planned)**
- Merchant API with HMAC request signing and an `Idempotency-Key` header
- Payment state machine: created, authorized, captured, failed, refunded
- Transactional outbox + Kafka, so events are never lost
- Signed webhooks with retries and a dead-letter queue
- Refunds as reversing ledger entries (allowed once), with a saga for failures
- Fraud rules, rate limiting with Redis
- Settlement batch jobs and bank reconciliation reports
- Observability: health probes, Prometheus metrics, structured logs
- Docker, Kubernetes, AWS deployment (Terraform), load and chaos tests

## Design decisions (and why)

| Decision | Why |
|---|---|
| Money as BIGINT paise | No floating-point or rounding errors |
| Ledger is append-only | History can never be rewritten; mistakes are fixed by reversing entries |
| Balance is calculated | A stored balance can go wrong; a calculated one cannot |
| Rules in Java and in the database | A bug in the code still cannot corrupt the books |
| Idempotency key everywhere money moves | Retries and duplicates are normal in payments |
| No remote calls inside a database transaction | Prevents long locks and half-saved states |
| Outbox pattern for events | Database change and event always happen together |
| Flyway only for schema changes | Every environment has the same, versioned schema |

## Ledger API (available now)

| Method | Path | What it does |
|---|---|---|
| POST | `/api/v1/ledger/accounts` | Create an account |
| GET | `/api/v1/ledger/accounts/{id}` | Read an account |
| GET | `/api/v1/ledger/accounts/{id}/balance` | Read a calculated balance |
| POST | `/api/v1/ledger/transactions` | Post a balanced transaction (idempotent) |

Errors always look like `{ "code", "message", "timestamp", "details" }` with the right
status: 400 bad input, 404 not found, 409 conflict, 422 unbalanced or wrong currency.

## Tech stack

Java 21, Spring Boot 3.5, Spring Data JPA, PostgreSQL, Flyway, Maven multi-module,
Docker Compose. Planned: Kafka, Redis, Testcontainers, Kubernetes, AWS, Terraform.

## Project layout

```
payment-gateway/
  common/  ledger-service/  payment-service/  bank-simulator/
  deploy/docker/   docs/PROJECT_RULES.md
ledger-service/src/main/java/com/sanjana/pay/ledger/
  controller  service  repository  entity  enums  dto  exception  mapper  config
```

## Run on your computer

1. Install Java 21, Maven, Docker and PostgreSQL.
2. Create the databases `ledger_db` and `payment_db`.
3. In each service, create `src/main/resources/application-local.yml` with your database
   username and password (this file is git-ignored; never commit secrets).
4. Run `mvn clean install`, then start `LedgerServiceApplication`.

## Roadmap

- [x] Project setup, database, Docker
- [x] Ledger service
- [ ] Automatic tests (Testcontainers)
- [ ] Merchant API, bank simulator, payment states
- [ ] Outbox, Kafka, webhooks
- [ ] Refunds and failure handling
- [ ] Gateway, JWT, fraud checks
- [ ] Settlement and reconciliation
- [ ] Frontend dashboard
- [ ] Kubernetes, AWS, load tests

## Author
Sanjana Kuhite , Java Full Stack Developer, Maharashtra.