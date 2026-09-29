package com.michaeldamico.gestionale.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.michaeldamico.gestionale.dto.ErroreResponse;

@RestControllerAdvice
public class GestoreErrori {

    @ExceptionHandler(RisorsaNonTrovataException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErroreResponse risorsaNonTrovata(RisorsaNonTrovataException e) {
        return new ErroreResponse(404, e.getMessage());
    }

    @ExceptionHandler(RegolaViolataException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErroreResponse regolaViolata(RegolaViolataException e) {
        return new ErroreResponse(409, e.getMessage());
    }
}
