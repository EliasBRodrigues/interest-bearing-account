package com.interest_bearing_account.server.entity;


import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "fechamento")
public class DailyClosing {
    @Id
    @Column(name = "data")
    private LocalDate date;

    @Column(nullable = false, length = 15)
    private String status;

    @Column(name = "contas_processadas", nullable = false)
    private long processedAccounts;

    @Column(name = "iniciado_em", nullable = false)
    private OffsetDateTime startedAt;

    @Column(name = "concluido_em")
    private OffsetDateTime completedAt;

    protected DailyClosing() {}

    public DailyClosing(LocalDate date, OffsetDateTime startedAt) {
        this.date = date;
        this.startedAt = startedAt;
        this.status = "EM_ANDAMENTO";
    }

    public String getStatus() {
        return status;
    }

    public long getProcessedAccounts() {
        return processedAccounts;
    }

    public void resume() {
        this.status = "EM_ANDAMENTO";
        this.completedAt = null;
    }

    public void complete(OffsetDateTime now) {
        this.status = "CONCLUIDO";
        this.completedAt = now;
    }

    public void fail() {
        if (!"CONCLUIDO".equals(status)) this.status = "ERRO";
    }
}