package com.interest_bearing_account.server.entity;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "lancamento",
        uniqueConstraints = @UniqueConstraint(columnNames = {"conta_id", "data_fechamento"}))
public class LedgerEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conta_id", nullable = false)
    private Account account;

    @Column(nullable = false, length = 10)
    private String tipo;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Column(name = "data_hora", nullable = false)
    private OffsetDateTime dataHora;

    @Column(name = "data_fechamento")
    private LocalDate dataFechamento;

    protected LedgerEntry() {}

    public LedgerEntry(
            Account account,
            String tipo,
            BigDecimal valor,
            OffsetDateTime dataHora,
            LocalDate dataFechamento) {
        this.account = account;
        this.tipo = tipo;
        this.valor = valor;
        this.dataHora = dataHora;
        this.dataFechamento = dataFechamento;
    }

    public Long getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public OffsetDateTime getDataHora() {
        return dataHora;
    }
}