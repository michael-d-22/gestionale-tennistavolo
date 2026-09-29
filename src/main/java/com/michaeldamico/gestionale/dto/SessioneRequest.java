package com.michaeldamico.gestionale.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.michaeldamico.gestionale.entity.Categoria;
import com.michaeldamico.gestionale.entity.TipoSessione;

import jakarta.validation.constraints.NotNull;

public record SessioneRequest(@NotNull LocalDate data,
                              @NotNull LocalTime ora,
                              @NotNull TipoSessione tipo,
                              Categoria categoria,
                              Integer capienza,
                              Long allenatoreId) {
}
