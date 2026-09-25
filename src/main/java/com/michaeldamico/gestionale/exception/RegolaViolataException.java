package com.michaeldamico.gestionale.exception;

public class RegolaViolataException extends RuntimeException {

    public RegolaViolataException(String messaggio) {
        super(messaggio);
    }
}
