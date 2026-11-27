package com.interest_bearing_account.server.domain;


import com.interest_bearing_account.server.entity.Account;
import com.interest_bearing_account.server.exception.ApiException;
import com.interest_bearing_account.server.repository.AccountRepository;
import com.interest_bearing_account.server.repository.EntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accounts;

    @Autowired
    private EntryRepository entries;

    public record Entry(long id, String tipo, OffsetDateTime dataHora, String valor) {}

    public record Statement(List<Entry> content, int page, int size, long totalElements) {}

    public record Balance(String contaId, String numero, String titular, String saldo) {}

    @Transactional(readOnly = true)
    public Balance balance(String id) {
        return view(accounts.findById(id).orElseThrow(this::missing));
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public Statement statement(String id, int page, int size) {
        if (page < 0 || size < 1 || size > 100)
            throw new ApiException(
                    422,
                    "PAGINACAO_INVALIDA", "A página começa em 0 e o tamanho deve estar entre 1 e 100.");
        var result =
                entries.findByAccount_Id(id,
                        PageRequest.of(
                                page,
                                size,
                                Sort.by(Sort.Order.desc("dataHora"),
                                        Sort.Order.desc("id"))));
        var rows = result.getContent().stream()
                        .map(e -> new Entry(e.getId(),
                                                e.getTipo(),
                                                e.getDataHora(),
                                                e.getValor().toPlainString())).toList();
        return new Statement(rows, page, size, result.getTotalElements());
    }

    private Balance view(Account a) {
        return new Balance(a.getId(), a.getNumero(), a.getTitular(), a.getSaldo().toPlainString());
    }

    private ApiException missing() {
        return new ApiException(404, "CONTA_NAO_ENCONTRADA", "Conta não encontrada.");
    }
}

