package com.interest_bearing_account.server.repository;

import com.interest_bearing_account.server.entity.DailyClosing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface ClosingRepository  extends JpaRepository<DailyClosing, LocalDate> {
    @Modifying(flushAutomatically = true)
    @Query("update DailyClosing c set c.processedAccounts=c.processedAccounts+1 where c.date=:date")
    int incrementProcessed(@Param("date") LocalDate date);
}
