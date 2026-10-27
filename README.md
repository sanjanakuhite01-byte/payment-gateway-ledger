# Payment Gateway + Double-Entry Ledger

A Razorpay/Stripe-style payment gateway built with Java 21, Spring Boot,
Postgres and Kafka. Every rupee is recorded in an immutable double-entry ledger.

## Modules
- common: shared classes (money, error codes)
- ledger-service: the books (debit equals credit, never edited)
- payment-service: merchant API and payment flow
- bank-simulator: fake bank for failure testing