package com.interest_bearing_account.server.controller;

import com.interest_bearing_account.server.exception.ApiException;
import com.interest_bearing_account.server.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class ServerController {

    @Autowired
    private AccountService accounts;

    @GetMapping("/contas/{id}/saldo")
    public AccountService.Balance balance(@PathVariable String id, Principal principal) {
        own(id, principal);
        return accounts.balance(id);
    }

    private void own(String id, Principal principal) {
        if (!id.equals(principal.getName()))
            throw new ApiException(403, "ACESSO_NEGADO", "Você só pode acessar a sua própria conta.");
    }

}
