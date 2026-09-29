package com.michaeldamico.gestionale.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;

import com.michaeldamico.gestionale.dto.PresenzeAtletaResponse;
import com.michaeldamico.gestionale.entity.StatoPrenotazione;
import com.michaeldamico.gestionale.repository.PrenotazioneRepository;

@Service
public class ReportService {

    private final PrenotazioneRepository prenotazioneRepository;

    public ReportService(PrenotazioneRepository prenotazioneRepository) {
        this.prenotazioneRepository = prenotazioneRepository;
    }

    public List<PresenzeAtletaResponse> presenzePerAtleta(YearMonth mese) {
        LocalDate inizio = mese.atDay(1);
        LocalDate fine = mese.atEndOfMonth();
        return prenotazioneRepository.contaPresenzePerAtleta(StatoPrenotazione.PRESENTE, inizio, fine);
    }
}
