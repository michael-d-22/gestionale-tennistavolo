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

import com.michaeldamico.gestionale.dto.AllenatoreRequest;
import com.michaeldamico.gestionale.dto.AllenatoreResponse;
import com.michaeldamico.gestionale.entity.Allenatore;
import com.michaeldamico.gestionale.service.AllenatoreService;

@RestController
@RequestMapping("/api/allenatori")
public class AllenatoreController {

    private final AllenatoreService allenatoreService;

    public AllenatoreController(AllenatoreService allenatoreService) {
        this.allenatoreService = allenatoreService;
    }

    @GetMapping
    public List<AllenatoreResponse> elenco() {
        List<AllenatoreResponse> risposta = new ArrayList<>();
        for (Allenatore allenatore : allenatoreService.trovaTutti()) {
            risposta.add(AllenatoreResponse.da(allenatore));
        }
        return risposta;
    }

    @GetMapping("/{id}")
    public AllenatoreResponse dettaglio(@PathVariable Long id) {
        return AllenatoreResponse.da(allenatoreService.trova(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AllenatoreResponse crea(@Valid @RequestBody AllenatoreRequest request) {
        return AllenatoreResponse.da(allenatoreService.crea(request));
    }
}
