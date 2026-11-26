package com.interest_bearing_account.server.repository;

import java.time.OffsetDateTime;
import java.util.Optional;

import com.interest_bearing_account.server.entity.SessionToken;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface SessionRepository extends JpaRepository<SessionToken, String> {
    @Query("select s.account.id from SessionToken s where s.tokenHash=:hash and s.expiraEm>:now")
    Optional<String> findValidAccount(@Param("hash") String hash, @Param("now") OffsetDateTime now);

    @Modifying
    @Query("delete from SessionToken s where s.expiraEm<=:now")
    int deleteExpired(@Param("now") OffsetDateTime now);
}