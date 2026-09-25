package com.michaeldamico.gestionale.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.michaeldamico.gestionale.entity.Atleta;
import com.michaeldamico.gestionale.entity.Prenotazione;
import com.michaeldamico.gestionale.entity.Sessione;
import com.michaeldamico.gestionale.entity.StatoPrenotazione;
import com.michaeldamico.gestionale.entity.TipoSessione;
import com.michaeldamico.gestionale.exception.RegolaViolataException;
import com.michaeldamico.gestionale.exception.RisorsaNonTrovataException;
import com.michaeldamico.gestionale.repository.AtletaRepository;
import com.michaeldamico.gestionale.repository.PrenotazioneRepository;
import com.michaeldamico.gestionale.repository.SessioneRepository;

@Service
public class PrenotazioneService {

    private final PrenotazioneRepository prenotazioneRepository;
    private final AtletaRepository atletaRepository;
    private final SessioneRepository sessioneRepository;

    public PrenotazioneService(PrenotazioneRepository prenotazioneRepository,
                               AtletaRepository atletaRepository,
                               SessioneRepository sessioneRepository) {
        this.prenotazioneRepository = prenotazioneRepository;
        this.atletaRepository = atletaRepository;
        this.sessioneRepository = sessioneRepository;
    }

    @Transactional
    public Prenotazione prenota(Long atletaId, Long sessioneId) {
        Atleta atleta = atletaRepository.findById(atletaId)
                .orElseThrow(() -> new RisorsaNonTrovataException("Atleta " + atletaId + " non trovato"));
        Sessione sessione = sessioneRepository.findById(sessioneId)
                .orElseThrow(() -> new RisorsaNonTrovataException("Sessione " + sessioneId + " non trovata"));

        if (isIniziata(sessione)) {
            throw new RegolaViolataException("La sessione è già iniziata o passata");
        }

        if (sessione.getCategoria() != null && sessione.getCategoria() != atleta.getCategoria()) {
            throw new RegolaViolataException("La sessione è riservata alla categoria " + sessione.getCategoria());
        }

        Prenotazione esistente = prenotazioneRepository
                .findByAtletaIdAndSessioneId(atletaId, sessioneId)
                .orElse(null);
        if (esistente != null && esistente.getStato() != StatoPrenotazione.ANNULLATA) {
            throw new RegolaViolataException("L'atleta è già prenotato a questa sessione");
        }

        long postiOccupati = prenotazioneRepository
                .countBySessioneIdAndStatoNot(sessioneId, StatoPrenotazione.ANNULLATA);
        if (postiOccupati >= sessione.getCapienza()) {
            throw new RegolaViolataException("La sessione è piena");
        }

        if (esistente != null) {
            esistente.riattiva();
            return prenotazioneRepository.save(esistente);
        }
        return prenotazioneRepository.save(new Prenotazione(atleta, sessione));
    }

    @Transactional
    public Prenotazione annulla(Long prenotazioneId) {
        Prenotazione prenotazione = trovaPrenotazione(prenotazioneId);

        if (prenotazione.getStato() == StatoPrenotazione.ANNULLATA) {
            throw new RegolaViolataException("La prenotazione è già annullata");
        }
        if (isIniziata(prenotazione.getSessione())) {
            throw new RegolaViolataException("Non si può annullare una sessione già iniziata");
        }

        prenotazione.annulla();
        return prenotazioneRepository.save(prenotazione);
    }

    @Transactional
    public Prenotazione segnaPresente(Long prenotazioneId) {
        Prenotazione prenotazione = trovaPrenotazioneRegistrabile(prenotazioneId);
        prenotazione.segnaPresente();
        return prenotazioneRepository.save(prenotazione);
    }

    @Transactional
    public Prenotazione segnaAssente(Long prenotazioneId) {
        Prenotazione prenotazione = trovaPrenotazioneRegistrabile(prenotazioneId);
        prenotazione.segnaAssente();
        return prenotazioneRepository.save(prenotazione);
    }

    private Prenotazione trovaPrenotazione(Long prenotazioneId) {
        return prenotazioneRepository.findById(prenotazioneId)
                .orElseThrow(() -> new RisorsaNonTrovataException("Prenotazione " + prenotazioneId + " non trovata"));
    }

    private Prenotazione trovaPrenotazioneRegistrabile(Long prenotazioneId) {
        Prenotazione prenotazione = trovaPrenotazione(prenotazioneId);

        if (prenotazione.getStato() == StatoPrenotazione.ANNULLATA) {
            throw new RegolaViolataException("Non si registra la presenza su una prenotazione annullata");
        }
        Sessione sessione = prenotazione.getSessione();
        if (sessione.getTipo() == TipoSessione.GRUPPO && !isIniziata(sessione)) {
            throw new RegolaViolataException("Le presenze di una sessione di gruppo si registrano dopo l'inizio");
        }
        return prenotazione;
    }

    private boolean isIniziata(Sessione sessione) {
        return !sessione.getInizio().isAfter(LocalDateTime.now());
    }
}
