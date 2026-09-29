package com.michaeldamico.gestionale.dto;

import java.time.YearMonth;

import com.michaeldamico.gestionale.entity.Categoria;

public record PercentualeAtletaResponse(Long atletaId,
                                        String nome,
                                        String cognome,
                                        Categoria categoria,
                                        YearMonth mese,
                                        long presenze,
                                        long sessioniCategoria,
                                        double percentuale) {
}
