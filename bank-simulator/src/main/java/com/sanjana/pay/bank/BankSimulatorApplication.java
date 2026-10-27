package com.sanjana.pay.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Starting point of the Bank Simulator.
 * WHAT: a fake bank that we control.
 * WHY: a real bank never lets you test timeouts, declines and duplicate
 *      callbacks on purpose. This one does, so we can prove the gateway stays safe.
 */
@SpringBootApplication
public class BankSimulatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankSimulatorApplication.class, args);
    }
}