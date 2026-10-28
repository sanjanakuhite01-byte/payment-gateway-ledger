package com.sanjana.pay.ledger.service;

import com.sanjana.pay.ledger.dto.request.CreateAccountRequest;
import com.sanjana.pay.ledger.dto.response.AccountResponse;
import com.sanjana.pay.ledger.dto.response.BalanceResponse;
import com.sanjana.pay.ledger.entity.Account;
import com.sanjana.pay.ledger.enums.Direction;
import com.sanjana.pay.ledger.exception.AccountNotFoundException;
import com.sanjana.pay.ledger.mapper.AccountMapper;
import com.sanjana.pay.ledger.repository.AccountRepository;
import com.sanjana.pay.ledger.repository.LedgerEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * WHAT: create accounts, read accounts, and calculate balances.
 * WHY: the balance is always calculated from the entries (never stored),
 *      so it can never be out of date.
 */
@Service
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final LedgerEntryRepository entryRepository;
    private final AccountMapper mapper;

    public AccountService(AccountRepository accountRepository,
                          LedgerEntryRepository entryRepository,
                          AccountMapper mapper) {
        this.accountRepository = accountRepository;
        this.entryRepository = entryRepository;
        this.mapper = mapper;
    }

    @Transactional
    public AccountResponse create(CreateAccountRequest request) {
        String id = "acc_" + UUID.randomUUID().toString().replace("-", "");
        // saveAndFlush: if the same account already exists, the database unique rule
        // fails right here, and the error handler answers 409.
        Account saved = accountRepository.saveAndFlush(new Account(
                id,
                request.ownerType(),
                request.ownerRef(),
                request.accountType(),
                request.name(),
                request.currency()));
        log.info("Created account id={} type={} currency={}", id, request.accountType(), request.currency());
        return mapper.toResponse(saved);
    }

    // readOnly = true: tells Spring and the database we will not change anything here.
    @Transactional(readOnly = true)
    public AccountResponse get(String id) {
        return mapper.toResponse(find(id));
    }

    @Transactional(readOnly = true)
    public BalanceResponse balance(String id) {
        Account account = find(id);
        long debit = entryRepository.sumAmount(id, Direction.DEBIT);
        long credit = entryRepository.sumAmount(id, Direction.CREDIT);
        // Natural balance: ASSET and EXPENSE grow on DEBIT; LIABILITY and REVENUE grow on CREDIT.
        long balance = switch (account.getAccountType()) {
            case ASSET, EXPENSE -> Math.subtractExact(debit, credit);
            case LIABILITY, REVENUE -> Math.subtractExact(credit, debit);
        };
        return new BalanceResponse(id, account.getCurrency(), debit, credit, balance);
    }

    private Account find(String id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }
}