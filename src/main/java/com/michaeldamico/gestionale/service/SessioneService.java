package com.michaeldamico.gestionale.service;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Sessione crea(LocalDate data, LocalTime ora, TipoSessione tipo,
                         Categoria categoria, Integer capienza, Long allenatoreId) {
        Allenatore allenatore = null;
        if (allenatoreId != null) {
            allenatore = allenatoreRepository.findById(allenatoreId)
                    .orElseThrow(() -> new RisorsaNonTrovataException("Allenatore " + allenatoreId + " non trovato"));
        }

        if (tipo == TipoSessione.INDIVIDUALE) {
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

        return sessioneRepository.save(new Sessione(data, ora, tipo, categoria, capienza, allenatore));
    }
}
