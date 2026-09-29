package com.michaeldamico.gestionale.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.michaeldamico.gestionale.dto.SessioneRequest;
import com.michaeldamico.gestionale.entity.Allenatore;
import com.michaeldamico.gestionale.entity.Categoria;
import com.michaeldamico.gestionale.entity.Sessione;
import com.michaeldamico.gestionale.entity.TipoSessione;
import com.michaeldamico.gestionale.exception.RegolaViolataException;
import com.michaeldamico.gestionale.exception.RisorsaNonTrovataException;
import com.michaeldamico.gestionale.repository.AllenatoreRepository;
import com.michaeldamico.gestionale.repository.SessioneRepository;

@Service
public class SessioneService {

    private final SessioneRepository sessioneRepository;
    private final AllenatoreRepository allenatoreRepository;

    public SessioneService(SessioneRepository sessioneRepository,
                           AllenatoreRepository allenatoreRepository) {
        this.sessioneRepository = sessioneRepository;
        this.allenatoreRepository = allenatoreRepository;
    }

    @Transactional
    public Sessione crea(SessioneRequest request) {
        Allenatore allenatore = null;
        if (request.allenatoreId() != null) {
            allenatore = allenatoreRepository.findById(request.allenatoreId())
                    .orElseThrow(() -> new RisorsaNonTrovataException("Allenatore " + request.allenatoreId() + " non trovato"));
        }

        Categoria categoria = request.categoria();
        Integer capienza = request.capienza();

        if (request.tipo() == TipoSessione.INDIVIDUALE) {
            capienza = 1;
            categoria = null;
        } else {
            if (categoria == null) {
                throw new RegolaViolataException("Una sessione di gruppo deve avere una categoria");
            }
            if (capienza == null || capienza < 1) {
                throw new RegolaViolataException("La capienza deve essere almeno 1");
            }
        }

        return sessioneRepository.save(new Sessione(request.data(), request.ora(), request.tipo(),
                categoria, capienza, allenatore));
    }

    public List<Sessione> trovaTutte() {
        return sessioneRepository.findAll();
    }

    public Sessione trova(Long id) {
        return sessioneRepository.findById(id)
                .orElseThrow(() -> new RisorsaNonTrovataException("Sessione " + id + " non trovata"));
    }
}
