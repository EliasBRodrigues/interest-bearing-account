package com.interest_bearing_account.server.controller;

import com.interest_bearing_account.server.domain.auth.AuthService;
import com.interest_bearing_account.server.exception.ApiException;
import com.interest_bearing_account.server.domain.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
public class ServerController {

    @Autowired
    private AuthService auth;

    @Autowired
    private AccountService accounts;

    @PostMapping("/auth/login")
    public AuthService.Login login(@RequestBody(required = false) Map<String, Object> body) {
        return auth.login(body);
    }

    @PostMapping("/contas")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthService.Created create(@RequestBody(required = false) Map<String, Object> body) {
        return auth.create(body);
    }

    @GetMapping("/contas/{id}/saldo")
    public AccountService.Balance balance(@PathVariable String id, Principal principal) {
        own(id, principal);
        return accounts.balance(id);
    }

    @GetMapping("/contas/{id}/extrato")
    public AccountService.Statement statement(
            @PathVariable String id,
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        own(id, principal);
        return accounts.statement(id, page, size);
    }

    @PostMapping("/contas/{id}/depositos")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountService.Balance deposit(
            @PathVariable String id,
            Principal principal,
            @RequestBody(required = false) Map<String, Object> body) {
        own(id, principal);
        return accounts.move(id, body == null ? null : body.get("valor"), false);
    }


    private void own(String id, Principal principal) {
        if (!id.equals(principal.getName()))
            throw new ApiException(403, "ACESSO_NEGADO", "Você só pode acessar a sua própria conta.");
    }

}
