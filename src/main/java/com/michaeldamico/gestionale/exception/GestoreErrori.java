package com.michaeldamico.gestionale.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroreResponse datiNonValidi(MethodArgumentNotValidException e) {
        List<String> errori = new ArrayList<>();
        for (FieldError errore : e.getBindingResult().getFieldErrors()) {
            errori.add(errore.getField() + ": " + errore.getDefaultMessage());
        }
        return new ErroreResponse(400, String.join(", ", errori));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroreResponse jsonNonLeggibile(HttpMessageNotReadableException e) {
        return new ErroreResponse(400, "Il corpo della richiesta non è un JSON valido o contiene valori non ammessi");
    }
}
