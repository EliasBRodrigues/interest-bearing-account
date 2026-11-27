package com.interest_bearing_account.server.repository;

import com.interest_bearing_account.server.entity.RateConfiguration;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;

public interface RateRepository extends JpaRepository<RateConfiguration, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from RateConfiguration c where c.id=1")
    Optional<RateConfiguration> findLocked();
}
