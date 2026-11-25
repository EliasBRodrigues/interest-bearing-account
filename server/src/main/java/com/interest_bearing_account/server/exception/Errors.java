package com.interest_bearing_account.server.exception;

import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class Errors {
    public record ErrorBody(String codigo, String mensagem) {}

    @ExceptionHandler(ApiException.class)
    ResponseEntity<ErrorBody> business(ApiException e) {
        return ResponseEntity.status(e.status()).body(new ErrorBody(e.codigo(), e.getMessage()));
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class
    })
    ResponseEntity<ErrorBody> malformed(Exception e) {
        return ResponseEntity.badRequest()
                .body(new ErrorBody(
                        "REQUISICAO_INVALIDA",
                        "Confira o JSON e o formato dos parâmetros enviados."));
    }

    @ExceptionHandler(DataAccessException.class)
    ResponseEntity<ErrorBody> database(DataAccessException e) {
        LoggerFactory.getLogger(Errors.class).error("Falha na operação de banco de dados", e);
        return ResponseEntity.status(503)
                .body(new ErrorBody(
                        "OPERACAO_INDISPONIVEL",
                        "Não foi possível concluir a operação. " +
                                " Atualize o saldo antes de tentar novamente."));
    }
}
