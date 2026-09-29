package com.michaeldamico.gestionale.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.michaeldamico.gestionale.entity.Categoria;
import com.michaeldamico.gestionale.entity.TipoSessione;

public record SessioneRequest(LocalDate data, LocalTime ora, TipoSessione tipo,
                              Categoria categoria, Integer capienza, Long allenatoreId) {
}
