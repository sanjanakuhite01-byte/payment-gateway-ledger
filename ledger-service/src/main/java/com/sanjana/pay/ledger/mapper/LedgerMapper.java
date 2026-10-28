package com.sanjana.pay.ledger.mapper;

import com.sanjana.pay.ledger.dto.response.EntryLineResponse;
import com.sanjana.pay.ledger.dto.response.TransactionResponse;
import com.sanjana.pay.ledger.entity.LedgerEntry;
import com.sanjana.pay.ledger.entity.LedgerTransaction;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * WHAT: converts entities into response DTOs.
 * WHY: entities stay inside the service. Only DTOs go out through the API.
 *      Keeping this in one class means the conversion is written once.
 */
@Component
public class LedgerMapper {

    public TransactionResponse toResponse(LedgerTransaction tx, List<LedgerEntry> entries) {
        List<EntryLineResponse> lines = entries.stream()
                .map(e -> new EntryLineResponse(e.getAccountId(), e.getDirection(), e.getAmount()))
                .toList();
        return new TransactionResponse(
                tx.getId(),
                tx.getPostingKey(),
                tx.getType(),
                tx.getReference(),
                tx.getDescription(),
                tx.getCurrency(),
                tx.getReversesId(),
                tx.getCreatedAt(),
                lines);
    }
}