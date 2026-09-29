package com.michaeldamico.gestionale.dto;

import com.michaeldamico.gestionale.entity.Categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtletaRequest(@NotBlank String nome,
                            @NotBlank String cognome,
                            @NotNull Categoria categoria) {
}
