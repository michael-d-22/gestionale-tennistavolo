package com.michaeldamico.gestionale.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.michaeldamico.gestionale.dto.AllenatoreRequest;
import com.michaeldamico.gestionale.entity.Allenatore;
import com.michaeldamico.gestionale.exception.RisorsaNonTrovataException;
import com.michaeldamico.gestionale.repository.AllenatoreRepository;

@Service
public class AllenatoreService {

    private final AllenatoreRepository allenatoreRepository;

    public AllenatoreService(AllenatoreRepository allenatoreRepository) {
        this.allenatoreRepository = allenatoreRepository;
    }

    @Transactional
    public Allenatore crea(AllenatoreRequest request) {
        return allenatoreRepository.save(new Allenatore(request.nome(), request.cognome()));
    }

    public List<Allenatore> trovaTutti() {
        return allenatoreRepository.findAll();
    }

    public Allenatore trova(Long id) {
        return allenatoreRepository.findById(id)
                .orElseThrow(() -> new RisorsaNonTrovataException("Allenatore " + id + " non trovato"));
    }
}
