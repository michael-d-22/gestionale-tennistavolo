package com.michaeldamico.gestionale.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.michaeldamico.gestionale.dto.PrenotazioneResponse;
import com.michaeldamico.gestionale.dto.SessioneRequest;
import com.michaeldamico.gestionale.dto.SessioneResponse;
import com.michaeldamico.gestionale.entity.Prenotazione;
import com.michaeldamico.gestionale.entity.Sessione;
import com.michaeldamico.gestionale.service.PrenotazioneService;
import com.michaeldamico.gestionale.service.SessioneService;

@RestController
@RequestMapping("/api/sessioni")
public class SessioneController {

    private final SessioneService sessioneService;
    private final PrenotazioneService prenotazioneService;

    public SessioneController(SessioneService sessioneService, PrenotazioneService prenotazioneService) {
        this.sessioneService = sessioneService;
        this.prenotazioneService = prenotazioneService;
    }

    @GetMapping
    public List<SessioneResponse> elenco() {
        List<SessioneResponse> risposta = new ArrayList<>();
        for (Sessione sessione : sessioneService.trovaTutte()) {
            risposta.add(SessioneResponse.da(sessione));
        }
        return risposta;
    }

    @GetMapping("/{id}")
    public SessioneResponse dettaglio(@PathVariable Long id) {
        return SessioneResponse.da(sessioneService.trova(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessioneResponse crea(@RequestBody SessioneRequest request) {
        return SessioneResponse.da(sessioneService.crea(request));
    }

    @GetMapping("/{id}/prenotazioni")
    public List<PrenotazioneResponse> prenotazioni(@PathVariable Long id) {
        List<PrenotazioneResponse> risposta = new ArrayList<>();
        for (Prenotazione prenotazione : prenotazioneService.trovaPerSessione(id)) {
            risposta.add(PrenotazioneResponse.da(prenotazione));
        }
        return risposta;
    }
}
