package com.michaeldamico.gestionale.dto;

import jakarta.validation.constraints.NotNull;

public record PrenotazioneRequest(@NotNull Long atletaId, @NotNull Long sessioneId) {
}
