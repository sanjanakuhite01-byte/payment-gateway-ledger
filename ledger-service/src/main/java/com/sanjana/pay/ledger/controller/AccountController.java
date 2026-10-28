package com.sanjana.pay.ledger.controller;

import com.sanjana.pay.ledger.dto.request.CreateAccountRequest;
import com.sanjana.pay.ledger.dto.response.AccountResponse;
import com.sanjana.pay.ledger.dto.response.BalanceResponse;
import com.sanjana.pay.ledger.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * WHAT: HTTP endpoints for accounts and balances.
 * WHY: thin on purpose. All logic is in AccountService.
 */
@RestController
@RequestMapping("/api/v1/ledger/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> create(@Valid @RequestBody CreateAccountRequest request) {
        AccountResponse created = accountService.create(request);
        // Location header = the address of the new account (good REST practice)
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public AccountResponse get(@PathVariable String id) {
        return accountService.get(id);
    }

    @GetMapping("/{id}/balance")
    public BalanceResponse balance(@PathVariable String id) {
        return accountService.balance(id);
    }
}