package com.interest_bearing_account.server.exception;

public class ApiException extends RuntimeException {
    private final int status;
    private final String codigo;

    public ApiException(int status, String codigo, String mensagem) {
        super(mensagem);
        this.status = status;
        this.codigo = codigo;
    }

    public int status() {
        return status;
    }

    public String codigo() {
        return codigo;
    }
}
