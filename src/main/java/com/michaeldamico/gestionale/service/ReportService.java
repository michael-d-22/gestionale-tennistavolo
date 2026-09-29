package com.michaeldamico.gestionale.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;

import com.michaeldamico.gestionale.dto.PercentualeAtletaResponse;
import com.michaeldamico.gestionale.dto.PresenzeAtletaResponse;
import com.michaeldamico.gestionale.dto.RiepilogoMeseResponse;
import com.michaeldamico.gestionale.entity.Atleta;
import com.michaeldamico.gestionale.entity.StatoPrenotazione;
import com.michaeldamico.gestionale.exception.RisorsaNonTrovataException;
import com.michaeldamico.gestionale.repository.AtletaRepository;
import com.michaeldamico.gestionale.repository.PrenotazioneRepository;
import com.michaeldamico.gestionale.repository.SessioneRepository;

@Service
public class ReportService {

    private final PrenotazioneRepository prenotazioneRepository;
    private final SessioneRepository sessioneRepository;
    private final AtletaRepository atletaRepository;

    public ReportService(PrenotazioneRepository prenotazioneRepository,
                         SessioneRepository sessioneRepository,
                         AtletaRepository atletaRepository) {
        this.prenotazioneRepository = prenotazioneRepository;
        this.sessioneRepository = sessioneRepository;
        this.atletaRepository = atletaRepository;
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

    public PercentualeAtletaResponse percentualeAtleta(Long atletaId, YearMonth mese) {
        Atleta atleta = atletaRepository.findById(atletaId)
                .orElseThrow(() -> new RisorsaNonTrovataException("Atleta " + atletaId + " non trovato"));
        LocalDate inizio = mese.atDay(1);
        LocalDate fine = mese.atEndOfMonth();

        long presenze = prenotazioneRepository.contaPresenzeAtletaInCategoria(
                atletaId, StatoPrenotazione.PRESENTE, atleta.getCategoria(), inizio, fine);
        long sessioniCategoria = sessioneRepository.countByCategoriaAndDataBetween(
                atleta.getCategoria(), inizio, fine);

        double percentuale = media(presenze * 100, sessioniCategoria);

        return new PercentualeAtletaResponse(atleta.getId(), atleta.getNome(), atleta.getCognome(),
                atleta.getCategoria(), mese, presenze, sessioniCategoria, percentuale);
    }

    private double media(long totale, long divisore) {
        if (divisore == 0) {
            return 0;
        }
        double risultato = (double) totale / divisore;
        return Math.round(risultato * 100) / 100.0;
    }
}
