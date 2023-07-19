package com.chatwave.accountservice.repository;

import com.chatwave.accountservice.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {}
