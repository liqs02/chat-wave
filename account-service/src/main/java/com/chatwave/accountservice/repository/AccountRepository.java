package com.chatwave.accountservice.repository;

import com.chatwave.accountservice.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByLoginName(String loginName);
    @Query("SELECT a FROM Account a WHERE a.loginName = ?1 OR a.displayName = ?2")
    Optional<Account> findByLoginOrDisplayName(String loginName, String displayName);
}
