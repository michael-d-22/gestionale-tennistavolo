package com.michaeldamico.gestionale.dto;

import com.michaeldamico.gestionale.entity.Atleta;
import com.michaeldamico.gestionale.entity.Categoria;

public record AtletaResponse(Long id, String nome, String cognome, Categoria categoria) {

    public static AtletaResponse da(Atleta atleta) {
        return new AtletaResponse(
                atleta.getId(),
                atleta.getNome(),
                atleta.getCognome(),
                atleta.getCategoria());
    }
}
