package com.michaeldamico.gestionale.dto;

import jakarta.validation.constraints.NotBlank;

public record AllenatoreRequest(@NotBlank String nome, @NotBlank String cognome) {
}
