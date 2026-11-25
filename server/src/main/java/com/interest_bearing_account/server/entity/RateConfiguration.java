package com.interest_bearing_account.server.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "configuracao")
public class RateConfiguration {
    @Id private Integer id;

    @Column(name = "taxa_diaria", precision = 14, scale = 10)
    private BigDecimal taxaDiaria;

    protected RateConfiguration() {}

    public BigDecimal getTaxaDiaria() {
        return taxaDiaria;
    }

    public void changeRate(BigDecimal value) {
        this.taxaDiaria = value;
    }
}