package com.interest_bearing_account.server.entity.utils;

import com.interest_bearing_account.server.exception.ApiException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Money {
    public static final BigDecimal MAX = new BigDecimal("99999999999999999.99");

    private Money() {}

    public static BigDecimal parse(Object raw) {
        if (!(raw instanceof String value)
                || value.length() > 22
                || !value.matches("[0-9]+(\\.[0-9]{1,2})?")) throw invalid();
        BigDecimal amount = new BigDecimal(value);
        if (amount.signum() <= 0 || amount.compareTo(MAX) > 0) throw invalid();
        return amount.setScale(2, RoundingMode.UNNECESSARY);
    }

    private static ApiException invalid() {
        return new ApiException(
                422,
                "VALOR_INVALIDO",
                "Informe um valor positivo como string, com ponto e até duas casas decimais.");
    }

    public static BigDecimal interest(BigDecimal balance, BigDecimal rate) {
        return rate == null
                ? new BigDecimal("0.00")
                : balance.multiply(rate).setScale(2, RoundingMode.HALF_EVEN);
    }

    public static void checkBalance(BigDecimal balance) {
        if (balance.compareTo(MAX) > 0)
            throw new ApiException(
                    422, "LIMITE_DE_SALDO", "O saldo ultrapassaria o limite suportado pela conta.");
    }
}