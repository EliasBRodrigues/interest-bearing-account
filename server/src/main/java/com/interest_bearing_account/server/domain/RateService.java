package com.interest_bearing_account.server.domain;

import com.interest_bearing_account.server.exception.ApiException;
import com.interest_bearing_account.server.repository.RateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class RateService {

    @Autowired
    private RateRepository rateRepository;

    public record Rate(String taxaDiaria) {}

    @Transactional(readOnly = true)
    public Rate get() {
        BigDecimal rate = rateRepository.findById(1).orElseThrow().getTaxaDiaria();
        if (rate == null)
            throw new ApiException(404, "TAXA_NAO_DEFINIDA", "Nenhuma taxa diária foi definida.");
        return view(rate);
    }

    private Rate view(BigDecimal rate) {
        return new Rate(rate.stripTrailingZeros().toPlainString());
    }
}
