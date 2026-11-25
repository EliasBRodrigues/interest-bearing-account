package com.interest_bearing_account.server.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "conta")
public class Account {
    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, unique = true, length = 30)
    private String numero;

    @Column(nullable = false, length = 120)
    private String titular;

    @Column(name = "senha_hash", nullable = false, length = 100)
    private String senhaHash;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo;

    @Column(name = "criada_em", nullable = false)
    private OffsetDateTime criadaEm;

    protected Account() {}

    public Account(
            String id, String numero, String titular, String senhaHash, OffsetDateTime criadaEm) {
        this.id = id;
        this.numero = numero;
        this.titular = titular;
        this.senhaHash = senhaHash;
        this.criadaEm = criadaEm;
        this.saldo = new BigDecimal("0.00");
    }

    public String getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public String getTitular() {
        return titular;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void changeBalance(BigDecimal value) {
        this.saldo = value;
    }
}