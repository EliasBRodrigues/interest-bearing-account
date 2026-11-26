package com.interest_bearing_account.server.domain;


import com.interest_bearing_account.server.entity.Account;
import com.interest_bearing_account.server.exception.ApiException;
import com.interest_bearing_account.server.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accounts;

    public record Balance(String contaId, String numero, String titular, String saldo) {}

    @Transactional(readOnly = true)
    public Balance balance(String id) {
        return view(accounts.findById(id).orElseThrow(this::missing));
    }

    private Balance view(Account a) {
        return new Balance(a.getId(), a.getNumero(), a.getTitular(), a.getSaldo().toPlainString());
    }

    private ApiException missing() {
        return new ApiException(404, "CONTA_NAO_ENCONTRADA", "Conta não encontrada.");
    }
}

