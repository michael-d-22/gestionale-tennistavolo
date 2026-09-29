package com.michaeldamico.gestionale.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;

import com.michaeldamico.gestionale.dto.PresenzeAtletaResponse;
import com.michaeldamico.gestionale.dto.RiepilogoMeseResponse;
import com.michaeldamico.gestionale.entity.StatoPrenotazione;
import com.michaeldamico.gestionale.repository.PrenotazioneRepository;
import com.michaeldamico.gestionale.repository.SessioneRepository;

@Service
public class ReportService {

    private final PrenotazioneRepository prenotazioneRepository;
    private final SessioneRepository sessioneRepository;

    public ReportService(PrenotazioneRepository prenotazioneRepository,
                         SessioneRepository sessioneRepository) {
        this.prenotazioneRepository = prenotazioneRepository;
        this.sessioneRepository = sessioneRepository;
    }

    public List<PresenzeAtletaResponse> presenzePerAtleta(YearMonth mese) {
        LocalDate inizio = mese.atDay(1);
        LocalDate fine = mese.atEndOfMonth();
        return prenotazioneRepository.contaPresenzePerAtleta(StatoPrenotazione.PRESENTE, inizio, fine);
    }

    public RiepilogoMeseResponse riepilogo(YearMonth mese) {
        LocalDate inizio = mese.atDay(1);
        LocalDate fine = mese.atEndOfMonth();

        long totaleSessioni = sessioneRepository.countByDataBetween(inizio, fine);
        long giorniAllenamento = sessioneRepository.contaGiorniDiAllenamento(inizio, fine);
        long presenzeTotali = prenotazioneRepository.countByStatoAndSessioneDataBetween(
                StatoPrenotazione.PRESENTE, inizio, fine);

        double mediaAlGiorno = media(presenzeTotali, giorniAllenamento);
        double mediaPerTurno = media(presenzeTotali, totaleSessioni);

        return new RiepilogoMeseResponse(mese, totaleSessioni, giorniAllenamento,
                presenzeTotali, mediaAlGiorno, mediaPerTurno);
    }

    private double media(long totale, long divisore) {
        if (divisore == 0) {
            return 0;
        }
        double risultato = (double) totale / divisore;
        return Math.round(risultato * 100) / 100.0;
    }
}
