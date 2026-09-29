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

import jakarta.validation.Valid;

import com.michaeldamico.gestionale.dto.AtletaRequest;
import com.michaeldamico.gestionale.dto.AtletaResponse;
import com.michaeldamico.gestionale.entity.Atleta;
import com.michaeldamico.gestionale.service.AtletaService;

@RestController
@RequestMapping("/api/atleti")
public class AtletaController {

    private final AtletaService atletaService;

    public AtletaController(AtletaService atletaService) {
        this.atletaService = atletaService;
    }

    @GetMapping
    public List<AtletaResponse> elenco() {
        List<AtletaResponse> risposta = new ArrayList<>();
        for (Atleta atleta : atletaService.trovaTutti()) {
            risposta.add(AtletaResponse.da(atleta));
        }
        return risposta;
    }

    @GetMapping("/{id}")
    public AtletaResponse dettaglio(@PathVariable Long id) {
        return AtletaResponse.da(atletaService.trova(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AtletaResponse crea(@Valid @RequestBody AtletaRequest request) {
        return AtletaResponse.da(atletaService.crea(request));
    }
}
