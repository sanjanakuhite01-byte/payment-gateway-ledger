package com.sanjana.pay.ledger.service;

import com.sanjana.pay.ledger.dto.request.EntryLineRequest;
import com.sanjana.pay.ledger.dto.request.PostTransactionRequest;
import com.sanjana.pay.ledger.dto.response.TransactionResponse;
import com.sanjana.pay.ledger.entity.Account;
import com.sanjana.pay.ledger.entity.LedgerEntry;
import com.sanjana.pay.ledger.entity.LedgerTransaction;
import com.sanjana.pay.ledger.enums.Direction;
import com.sanjana.pay.ledger.exception.AccountNotFoundException;
import com.sanjana.pay.ledger.exception.AlreadyReversedException;
import com.sanjana.pay.ledger.exception.CurrencyMismatchException;
import com.sanjana.pay.ledger.exception.PostingKeyConflictException;
import com.sanjana.pay.ledger.exception.TransactionNotFoundException;
import com.sanjana.pay.ledger.exception.UnbalancedTransactionException;
import com.sanjana.pay.ledger.mapper.LedgerMapper;
import com.sanjana.pay.ledger.repository.AccountRepository;
import com.sanjana.pay.ledger.repository.LedgerEntryRepository;
import com.sanjana.pay.ledger.repository.LedgerTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * WHAT: posts money movements into the ledger.
 * WHY: all ledger rules are in this one class:
 *      repeat-safe (posting key), balanced, same currency, all-or-nothing save.
 */
@Service
public class PostingService {

    private static final Logger log = LoggerFactory.getLogger(PostingService.class);

    private final AccountRepository accountRepository;
    private final LedgerTransactionRepository transactionRepository;
    private final LedgerEntryRepository entryRepository;
    private final LedgerMapper mapper;
    private final TransactionTemplate transactionTemplate;

    // Constructor injection (project rule): Spring gives us everything we need.
    public PostingService(AccountRepository accountRepository,
                          LedgerTransactionRepository transactionRepository,
                          LedgerEntryRepository entryRepository,
                          LedgerMapper mapper,
                          PlatformTransactionManager transactionManager) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.entryRepository = entryRepository;
        this.mapper = mapper;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public PostingResult post(PostTransactionRequest request) {
        // 1. Same key already posted? Return the old result (or refuse if the body differs).
        var existing = transactionRepository.findByPostingKey(request.postingKey());
        if (existing.isPresent()) {
            return replay(existing.get(), request);
        }

        try {
            // 2. New posting: everything below is saved in ONE database transaction.
            TransactionResponse created = transactionTemplate.execute(status -> create(request));
            return new PostingResult(created, false);
        } catch (DataIntegrityViolationException e) {
            // Two requests with the same key arrived at the same moment.
            // The database allowed only one. Read the winner and treat ours as a repeat.
            return transactionRepository.findByPostingKey(request.postingKey())
                    .map(winner -> replay(winner, request))
                    .orElseThrow(() -> e);
        }
    }

    private TransactionResponse create(PostTransactionRequest request) {
        // Rule: balanced. Math.addExact throws an error instead of silently overflowing.
        long debit = total(request, Direction.DEBIT);
        long credit = total(request, Direction.CREDIT);
        if (debit != credit) {
            throw new UnbalancedTransactionException(debit, credit);
        }

        checkAccounts(request);
        String reversesId = blankToNull(request.reversesId());
        checkReversal(reversesId);

        String id = "txn_" + UUID.randomUUID().toString().replace("-", "");

        // saveAndFlush: write the transaction first, because the lines point to it (foreign key).
        LedgerTransaction transaction = transactionRepository.saveAndFlush(new LedgerTransaction(
                id,
                request.postingKey(),
                request.type(),
                request.reference(),
                request.description(),
                request.currency(),
                reversesId));

        List<LedgerEntry> lines = request.entries().stream()
                .map(l -> new LedgerEntry(id, l.accountId(), l.direction(), l.amount()))
                .toList();
        List<LedgerEntry> savedLines = entryRepository.saveAll(lines);

        log.info("Posted transaction id={} type={} currency={} total={}",
                id, request.type(), request.currency(), debit);

        return mapper.toResponse(transaction, savedLines);
    }

    private long total(PostTransactionRequest request, Direction direction) {
        return request.entries().stream()
                .filter(l -> l.direction() == direction)
                .mapToLong(EntryLineRequest::amount)
                .reduce(0L, Math::addExact);
    }

    private void checkAccounts(PostTransactionRequest request) {
        Set<String> ids = request.entries().stream()
                .map(EntryLineRequest::accountId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Map<String, Account> found = accountRepository.findAllByIdIn(ids).stream()
                .collect(Collectors.toMap(Account::getId, Function.identity()));

        for (String accountId : ids) {
            Account account = found.get(accountId);
            if (account == null) {
                throw new AccountNotFoundException(accountId);
            }
            if (!account.getCurrency().equals(request.currency())) {
                throw new CurrencyMismatchException(accountId, account.getCurrency(), request.currency());
            }
        }
    }

    // Rule: a transaction can be reversed only once, and only if it exists.
    // (The "lines must mirror the original" check comes in the refund step.)
    private void checkReversal(String reversesId) {
        if (reversesId == null) {
            return;
        }
        if (!transactionRepository.existsById(reversesId)) {
            throw new TransactionNotFoundException(reversesId);
        }
        if (transactionRepository.existsByReversesId(reversesId)) {
            throw new AlreadyReversedException(reversesId);
        }
    }

    private PostingResult replay(LedgerTransaction existing, PostTransactionRequest request) {
        List<LedgerEntry> stored = entryRepository.findByTransactionId(existing.getId());
        if (!sameContent(existing, stored, request)) {
            throw new PostingKeyConflictException(request.postingKey());
        }
        return new PostingResult(mapper.toResponse(existing, stored), true);
    }

    // Same key is fine only when the body is the same too.
    private boolean sameContent(LedgerTransaction existing, List<LedgerEntry> stored,
                                PostTransactionRequest request) {
        List<String> storedLines = stored.stream()
                .map(e -> e.getAccountId() + "|" + e.getDirection() + "|" + e.getAmount())
                .sorted()
                .toList();
        List<String> requestLines = request.entries().stream()
                .map(l -> l.accountId() + "|" + l.direction() + "|" + l.amount())
                .sorted()
                .toList();

        return existing.getType().equals(request.type())
                && existing.getCurrency().equals(request.currency())
                && Objects.equals(existing.getReference(), request.reference())
                && Objects.equals(existing.getReversesId(), blankToNull(request.reversesId()))
                && storedLines.equals(requestLines);
    }

    private String blankToNull(String value) {
        return StringUtils.hasText(value) ? value : null;
    }
}