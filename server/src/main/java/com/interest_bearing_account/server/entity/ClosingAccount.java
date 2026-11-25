package com.interest_bearing_account.server.entity;


import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "fechamento_conta")
public class ClosingAccount {
    @EmbeddedId private ClosingAccountId id;

    @Column(name = "processada", nullable = false)
    private boolean processed;

    @Column(name = "taxa_aplicada", precision = 14, scale = 10)
    private BigDecimal appliedRate;

    @Column(name = "juros", precision = 19, scale = 2)
    private BigDecimal interest;

    protected ClosingAccount() {}

    public boolean isProcessed() {
        return processed;
    }

    public void finish(BigDecimal rate, BigDecimal interest) {
        this.appliedRate = rate;
        this.interest = interest;
        this.processed = true;
    }
}