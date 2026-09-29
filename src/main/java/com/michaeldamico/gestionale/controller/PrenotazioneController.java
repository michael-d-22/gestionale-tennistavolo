package com.michaeldamico.gestionale.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.michaeldamico.gestionale.dto.PrenotazioneRequest;
import com.michaeldamico.gestionale.dto.PrenotazioneResponse;
import com.michaeldamico.gestionale.service.PrenotazioneService;

@RestController
@RequestMapping("/api/prenotazioni")
public class PrenotazioneController {

    private final PrenotazioneService prenotazioneService;

    public PrenotazioneController(PrenotazioneService prenotazioneService) {
        this.prenotazioneService = prenotazioneService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrenotazioneResponse prenota(@RequestBody PrenotazioneRequest request) {
        return PrenotazioneResponse.da(prenotazioneService.prenota(request.atletaId(), request.sessioneId()));
    }

    @PostMapping("/{id}/annullamento")
    public PrenotazioneResponse annulla(@PathVariable Long id) {
        return PrenotazioneResponse.da(prenotazioneService.annulla(id));
    }

    @PostMapping("/{id}/presenza")
    public PrenotazioneResponse segnaPresente(@PathVariable Long id) {
        return PrenotazioneResponse.da(prenotazioneService.segnaPresente(id));
    }

    @PostMapping("/{id}/assenza")
    public PrenotazioneResponse segnaAssente(@PathVariable Long id) {
        return PrenotazioneResponse.da(prenotazioneService.segnaAssente(id));
    }
}
