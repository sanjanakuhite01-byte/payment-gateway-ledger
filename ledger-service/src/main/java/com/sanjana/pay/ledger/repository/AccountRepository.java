package com.sanjana.pay.ledger.repository;

import com.sanjana.pay.ledger.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/**
 * WHAT: database access for accounts.
 * WHY: JpaRepository gives save, findById and more for free.
 *      We only add what we need: load several accounts in one query.
 */
public interface AccountRepository extends JpaRepository<Account, String> {

    List<Account> findAllByIdIn(Collection<String> ids);
}