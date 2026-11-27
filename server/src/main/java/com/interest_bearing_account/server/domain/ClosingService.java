package com.interest_bearing_account.server.domain;

import com.interest_bearing_account.server.entity.DailyClosing;
import com.interest_bearing_account.server.exception.ApiException;
import com.interest_bearing_account.server.repository.ClosingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class ClosingService {

    @Autowired
    private ClosingRepository closingRepository;

    public record Closing(String status, long contasProcessadas) {}

    @Transactional(readOnly = true)
    public Closing get(LocalDate date) {
        DailyClosing closing =
                closingRepository
                        .findById(date)
                        .orElseThrow(() -> new ApiException(
                                404,
                                "FECHAMENTO_NAO_ENCONTRADO",
                                "Esta data ainda não foi processada."));
        return new Closing(closing.getStatus(), closing.getProcessedAccounts());
    }
}
