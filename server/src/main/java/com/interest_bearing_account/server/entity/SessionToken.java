package com.interest_bearing_account.server.entity;


import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "sessao")
public class SessionToken {
    @Id
    @Column(name = "token_hash", length = 64)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conta_id", nullable = false)
    private Account account;

    @Column(name = "expira_em", nullable = false)
    private OffsetDateTime expiraEm;

    protected SessionToken() {}

    public SessionToken(String tokenHash, Account account, OffsetDateTime expiraEm) {
        this.tokenHash = tokenHash;
        this.account = account;
        this.expiraEm = expiraEm;
    }

    public void expireAt(OffsetDateTime value) {
        this.expiraEm = value;
    }
}