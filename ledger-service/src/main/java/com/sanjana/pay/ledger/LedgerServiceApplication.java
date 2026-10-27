package com.sanjana.pay.ledger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Starting point of the Ledger service.
 * WHAT: boots Spring and scans this package and all packages below it.
 * WHY: the ledger is the books of the gateway. It runs as its own service so
 *      only this service can write money records.
 */
@SpringBootApplication
public class LedgerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LedgerServiceApplication.class, args);
    }
}