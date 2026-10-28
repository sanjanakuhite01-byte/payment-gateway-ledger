package com.sanjana.pay.ledger.controller;

import com.sanjana.pay.ledger.dto.request.PostTransactionRequest;
import com.sanjana.pay.ledger.dto.response.TransactionResponse;
import com.sanjana.pay.ledger.service.PostingResult;
import com.sanjana.pay.ledger.service.PostingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * WHAT: HTTP endpoint to post a transaction into the ledger.
 * WHY: thin on purpose. It only receives the request and returns the answer.
 *      @Valid runs the checks written in the request DTO (amount above zero,
 *      at least 2 lines, and so on) before our code runs.
 *      /api/v1 in the path lets us add a v2 later without breaking old callers.
 */
@RestController
@RequestMapping("/api/v1/ledger/transactions")
public class TransactionController {

    private final PostingService postingService;

    public TransactionController(PostingService postingService) {
        this.postingService = postingService;
    }

    // 201 Created = new transaction posted.
    // 200 OK      = same posting key was sent before; this is the old result.
    @PostMapping
    public ResponseEntity<TransactionResponse> post(@Valid @RequestBody PostTransactionRequest request) {
        PostingResult result = postingService.post(request);
        HttpStatus status = result.replayed() ? HttpStatus.OK : HttpStatus.CREATED;
        return ResponseEntity.status(status).body(result.transaction());
    }
}