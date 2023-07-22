package com.chatwave.accountservice.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table( name = "accounts" )
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    @Column(nullable = false, unique = true, updatable = false, length = 30)
    String loginName;

    @Column(nullable = false, unique = true, length = 30)
    String displayName;
}
