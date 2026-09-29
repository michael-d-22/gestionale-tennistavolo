package com.michaeldamico.gestionale.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.michaeldamico.gestionale.entity.Atleta;
import com.michaeldamico.gestionale.entity.Categoria;
import com.michaeldamico.gestionale.entity.Prenotazione;
import com.michaeldamico.gestionale.entity.Sessione;
import com.michaeldamico.gestionale.entity.TipoSessione;
import com.michaeldamico.gestionale.exception.RegolaViolataException;
import com.michaeldamico.gestionale.exception.RisorsaNonTrovataException;
import com.michaeldamico.gestionale.service.PrenotazioneService;

@WebMvcTest(PrenotazioneController.class)
class PrenotazioneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PrenotazioneService prenotazioneService;

    @Test
    void prenotazioneValidaRisponde201ConLaPrenotazione() throws Exception {
        when(prenotazioneService.prenota(1L, 2L)).thenReturn(prenotazioneDiGruppo());

        mockMvc.perform(post("/api/prenotazioni")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"atletaId\": 1, \"sessioneId\": 2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.stato").value("PRENOTATA"));
    }

    @Test
    void campoMancanteRisponde400SenzaChiamareIlService() throws Exception {
        mockMvc.perform(post("/api/prenotazioni")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"atletaId\": 1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(prenotazioneService, never()).prenota(any(), any());
    }

    @Test
    void jsonMalformatoRisponde400() throws Exception {
        mockMvc.perform(post("/api/prenotazioni")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"atletaId\": \"uno\""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void atletaInesistenteRisponde404ConIlMessaggio() throws Exception {
        when(prenotazioneService.prenota(99L, 2L))
                .thenThrow(new RisorsaNonTrovataException("Atleta 99 non trovato"));

        mockMvc.perform(post("/api/prenotazioni")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"atletaId\": 99, \"sessioneId\": 2}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.messaggio").value("Atleta 99 non trovato"));
    }

    @Test
    void sessionePienaRisponde409ConIlMessaggio() throws Exception {
        when(prenotazioneService.prenota(1L, 2L))
                .thenThrow(new RegolaViolataException("La sessione è piena"));

        mockMvc.perform(post("/api/prenotazioni")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"atletaId\": 1, \"sessioneId\": 2}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.messaggio").value("La sessione è piena"));
    }

    @Test
    void annullamentoRisponde200ConStatoAnnullata() throws Exception {
        Prenotazione annullata = prenotazioneDiGruppo();
        annullata.annulla();
        when(prenotazioneService.annulla(5L)).thenReturn(annullata);

        mockMvc.perform(post("/api/prenotazioni/5/annullamento"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stato").value("ANNULLATA"));
    }

    private Prenotazione prenotazioneDiGruppo() {
        Atleta atleta = new Atleta("Mario", "Rossi", Categoria.GIOVANILE);
        Sessione sessione = new Sessione(LocalDate.of(2026, 10, 2), LocalTime.of(18, 30),
                TipoSessione.GRUPPO, Categoria.GIOVANILE, 10, null);
        return new Prenotazione(atleta, sessione);
    }
}
