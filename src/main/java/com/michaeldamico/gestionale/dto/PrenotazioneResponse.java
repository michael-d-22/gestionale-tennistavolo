package com.michaeldamico.gestionale.dto;

import com.michaeldamico.gestionale.entity.Prenotazione;
import com.michaeldamico.gestionale.entity.StatoPrenotazione;

public record PrenotazioneResponse(Long id, Long atletaId, Long sessioneId, StatoPrenotazione stato) {

    public static PrenotazioneResponse da(Prenotazione prenotazione) {
        return new PrenotazioneResponse(
                prenotazione.getId(),
                prenotazione.getAtleta().getId(),
                prenotazione.getSessione().getId(),
                prenotazione.getStato());
    }
}
