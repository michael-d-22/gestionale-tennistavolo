package com.michaeldamico.gestionale.dto;

import com.michaeldamico.gestionale.entity.Categoria;

public record AtletaRequest(String nome, String cognome, Categoria categoria) {
}
