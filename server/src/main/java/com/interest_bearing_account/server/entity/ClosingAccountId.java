package com.interest_bearing_account.server.entity;


import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
public class ClosingAccountId implements Serializable {
    @Column(name = "data", nullable = false)
    private LocalDate date;

    @Column(name = "conta_id", nullable = false, length = 36)
    private String accountId;

    protected ClosingAccountId() {}

    public ClosingAccountId(LocalDate date, String accountId) {
        this.date = date;
        this.accountId = accountId;
    }

    @Override
    public boolean equals(Object other) {
        return this == other
                || other instanceof ClosingAccountId that
                && Objects.equals(date, that.date)
                && Objects.equals(accountId, that.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, accountId);
    }
}