package com.michaeldamico.gestionale.dto;

import com.michaeldamico.gestionale.entity.Allenatore;

public record AllenatoreResponse(Long id, String nome, String cognome) {

    public static AllenatoreResponse da(Allenatore allenatore) {
        return new AllenatoreResponse(
                allenatore.getId(),
                allenatore.getNome(),
                allenatore.getCognome());
    }
}
