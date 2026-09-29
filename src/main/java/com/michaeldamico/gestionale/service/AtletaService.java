package com.michaeldamico.gestionale.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.michaeldamico.gestionale.dto.AtletaRequest;
import com.michaeldamico.gestionale.entity.Atleta;
import com.michaeldamico.gestionale.exception.RisorsaNonTrovataException;
import com.michaeldamico.gestionale.repository.AtletaRepository;

@Service
public class AtletaService {

    private final AtletaRepository atletaRepository;

    public AtletaService(AtletaRepository atletaRepository) {
        this.atletaRepository = atletaRepository;
    }

    @Transactional
    public Atleta crea(AtletaRequest request) {
        return atletaRepository.save(new Atleta(request.nome(), request.cognome(), request.categoria()));
    }

    public List<Atleta> trovaTutti() {
        return atletaRepository.findAll();
    }

    public Atleta trova(Long id) {
        return atletaRepository.findById(id)
                .orElseThrow(() -> new RisorsaNonTrovataException("Atleta " + id + " non trovato"));
    }
}
