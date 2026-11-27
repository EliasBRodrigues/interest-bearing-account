package com.interest_bearing_account.server.repository;


import com.interest_bearing_account.server.entity.LedgerEntry;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface EntryRepository extends JpaRepository<LedgerEntry, Long> {
    Page<LedgerEntry> findByAccount_Id(String accountId, Pageable pageable);

    long countByTipo(String tipo);

    @Query("select coalesce(sum(e.valor),0) from LedgerEntry e where e.account.id=:accountId")
    BigDecimal sumByAccountId(@Param("accountId") String accountId);
}
