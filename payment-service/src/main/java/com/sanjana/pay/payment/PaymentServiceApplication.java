package com.sanjana.pay.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Starting point of the Payment service.
 * WHAT: boots Spring and scans this package and all packages below it.
 * WHY: this service is the merchant API and the payment flow (create, capture,
 *      void). Merchants call it, and it calls the Ledger and the bank.
 */
@SpringBootApplication
public class PaymentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}