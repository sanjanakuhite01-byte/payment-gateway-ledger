package com.sanjana.pay.ledger.mapper;

import com.sanjana.pay.ledger.dto.response.AccountResponse;
import com.sanjana.pay.ledger.entity.Account;
import org.springframework.stereotype.Component;

/** WHAT: converts an Account entity into an AccountResponse DTO. */
@Component
public class AccountMapper {

    public AccountResponse toResponse(Account a) {
        return new AccountResponse(
                a.getId(),
                a.getOwnerType(),
                a.getOwnerRef(),
                a.getAccountType(),
                a.getName(),
                a.getCurrency(),
                a.getCreatedAt());
    }
}