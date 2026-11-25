package com.interest_bearing_account.server.repository;

import com.interest_bearing_account.server.entity.Account;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByNumero(String numero);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.id=:id")
    Optional<Account> findLockedById(@Param("id") String id);

    @Query(value = "SELECT NEXT VALUE FOR numero_conta", nativeQuery = true)
    Long nextAccountNumber();
}
