package com.chatwave.accountservice.repository;

import com.chatwave.accountservice.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByLoginName(String loginName);
}
