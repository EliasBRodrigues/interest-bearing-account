package com.interest_bearing_account.server.domain.auth;


import com.interest_bearing_account.server.entity.Account;
import com.interest_bearing_account.server.entity.SessionToken;
import com.interest_bearing_account.server.exception.ApiException;
import com.interest_bearing_account.server.repository.AccountRepository;
import com.interest_bearing_account.server.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private final Clock clock;
    private final AccountRepository accounts;
    private final SessionRepository sessions;
    private final BCryptPasswordEncoder passwords = new BCryptPasswordEncoder(12);
    private final SecureRandom random = new SecureRandom();

    public AuthService(Clock clock, AccountRepository accounts, SessionRepository sessions) {
        this.clock = clock;
        this.accounts = accounts;
        this.sessions = sessions;
    }

    public record Created(String id, String numero, String titular) {}

    public record Login(String token, String contaId, OffsetDateTime expiraEm) {}

    private static String text(Map<String, Object> body, String key) {
        return body != null && body.get(key) instanceof String s ? s : "";
    }

    @Transactional
    public Created create(Map<String, Object> body) {
        String owner = text(body, "titular").strip(), password = text(body, "senha");
        if (owner.isBlank() || owner.length() > 120)
            throw new ApiException(422, "TITULAR_INVALIDO", "Informe o titular com até 120 caracteres.");
        if (password.length() < 6 || password.getBytes(StandardCharsets.UTF_8).length > 72)
            throw new ApiException(
                    422, "SENHA_INVALIDA", "Use uma senha de no mínimo 6 caracteres e até 72 bytes UTF-8.");
        Account account =
                accounts.save(
                        new Account(
                                UUID.randomUUID().toString(),
                                accounts.nextAccountNumber().toString(),
                                owner,
                                passwords.encode(password),
                                OffsetDateTime.now(clock)));
        return new Created(account.getId(), account.getNumero(), account.getTitular());
    }

    @Transactional
    public Login login(Map<String, Object> body) {
        String password = text(body, "senha");
        Account account = accounts.findByNumero(text(body, "numero")).orElse(null);
        if (account == null
                || password.getBytes(StandardCharsets.UTF_8).length > 72
                || !passwords.matches(password, account.getSenhaHash()))
            throw new ApiException(401, "NAO_AUTENTICADO", "Número da conta ou senha incorretos.");
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        OffsetDateTime expires = OffsetDateTime.now(clock).plusHours(1);
        sessions.deleteExpired(OffsetDateTime.now(clock));
        sessions.save(new SessionToken(hash(token), account, expires));
        return new Login(token, account.getId(), expires);
    }

    @Transactional(readOnly = true)
    public String authenticate(String token) {
        if (token.length() != 43) return null;
        return sessions.findValidAccount(hash(token), OffsetDateTime.now(clock)).orElse(null);
    }

    private String hash(String token) {
        try {
            return HexFormat.of()
                    .formatHex(
                            MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}