package com.chatwave.accountservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table( name = "accounts" )
public class Account {
    @Id
    private Integer id;

    @Column(nullable = false, unique = true, length = 30)
    private String displayName;
}
