package com.michaeldamico.gestionale.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.michaeldamico.gestionale.entity.Categoria;
import com.michaeldamico.gestionale.entity.Sessione;
import com.michaeldamico.gestionale.entity.TipoSessione;

public record SessioneResponse(Long id, LocalDate data, LocalTime ora, TipoSessione tipo,
                               Categoria categoria, Integer capienza, Long allenatoreId) {

    public static SessioneResponse da(Sessione sessione) {
        Long allenatoreId = null;
        if (sessione.getAllenatore() != null) {
            allenatoreId = sessione.getAllenatore().getId();
        }

        return new SessioneResponse(
                sessione.getId(),
                sessione.getData(),
                sessione.getOra(),
                sessione.getTipo(),
                sessione.getCategoria(),
                sessione.getCapienza(),
                allenatoreId);
    }
}
