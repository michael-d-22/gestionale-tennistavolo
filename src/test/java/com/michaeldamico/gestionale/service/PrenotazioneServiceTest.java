package com.michaeldamico.gestionale.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.michaeldamico.gestionale.entity.Atleta;
import com.michaeldamico.gestionale.entity.Categoria;
import com.michaeldamico.gestionale.entity.Prenotazione;
import com.michaeldamico.gestionale.entity.Sessione;
import com.michaeldamico.gestionale.entity.StatoPrenotazione;
import com.michaeldamico.gestionale.entity.TipoSessione;
import com.michaeldamico.gestionale.exception.RegolaViolataException;
import com.michaeldamico.gestionale.exception.RisorsaNonTrovataException;
import com.michaeldamico.gestionale.repository.AtletaRepository;
import com.michaeldamico.gestionale.repository.PrenotazioneRepository;
import com.michaeldamico.gestionale.repository.SessioneRepository;

@ExtendWith(MockitoExtension.class)
class PrenotazioneServiceTest {

    // "Adesso", per tutti i test: 1 ottobre 2026 alle 18:00
    private static final ZoneId ZONA = ZoneId.of("Europe/Rome");
    private static final LocalDateTime ADESSO = LocalDateTime.of(2026, 10, 1, 18, 0);

    @Mock
    private PrenotazioneRepository prenotazioneRepository;
    @Mock
    private AtletaRepository atletaRepository;
    @Mock
    private SessioneRepository sessioneRepository;

    private PrenotazioneService service;

    private Atleta rossi;

    @BeforeEach
    void preparaService() {
        Clock orologioFermo = Clock.fixed(ADESSO.atZone(ZONA).toInstant(), ZONA);
        service = new PrenotazioneService(prenotazioneRepository, atletaRepository, sessioneRepository, orologioFermo);
        rossi = new Atleta("Mario", "Rossi", Categoria.GIOVANILE);
    }

    // ---------- prenota ----------

    @Test
    void prenotaSessioneDiGruppoCreaPrenotazionePrenotata() {
        Sessione sessione = sessioneGruppo(ADESSO.plusDays(1), 10);
        preparaAtletaESessione(sessione);
        when(prenotazioneRepository.findByAtletaIdAndSessioneId(1L, 2L)).thenReturn(Optional.empty());
        when(prenotazioneRepository.countBySessioneIdAndStatoNot(2L, StatoPrenotazione.ANNULLATA)).thenReturn(3L);
        restituisciCioCheVieneSalvato();

        Prenotazione risultato = service.prenota(1L, 2L);

        assertEquals(StatoPrenotazione.PRENOTATA, risultato.getStato());
        assertSame(rossi, risultato.getAtleta());
    }

    @Test
    void prenotaSessioneIndividualeNascePresente() {
        Sessione sessione = sessioneIndividuale(ADESSO.plusDays(1));
        preparaAtletaESessione(sessione);
        when(prenotazioneRepository.findByAtletaIdAndSessioneId(1L, 2L)).thenReturn(Optional.empty());
        when(prenotazioneRepository.countBySessioneIdAndStatoNot(2L, StatoPrenotazione.ANNULLATA)).thenReturn(0L);
        restituisciCioCheVieneSalvato();

        Prenotazione risultato = service.prenota(1L, 2L);

        assertEquals(StatoPrenotazione.PRESENTE, risultato.getStato());
    }

    @Test
    void prenotaConAtletaInesistenteLanciaRisorsaNonTrovata() {
        when(atletaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RisorsaNonTrovataException.class, () -> service.prenota(99L, 2L));
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void prenotaSessioneGiaIniziataVieneRifiutata() {
        Sessione sessione = sessioneGruppo(ADESSO.minusHours(1), 10);
        preparaAtletaESessione(sessione);

        assertThrows(RegolaViolataException.class, () -> service.prenota(1L, 2L));
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void prenotaSessioneDiAltraCategoriaVieneRifiutata() {
        Sessione sessione = new Sessione(ADESSO.plusDays(1).toLocalDate(), ADESSO.toLocalTime(),
                TipoSessione.GRUPPO, Categoria.AGONISTI, 10, null);
        preparaAtletaESessione(sessione);

        assertThrows(RegolaViolataException.class, () -> service.prenota(1L, 2L));
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void prenotaDueVolteLaStessaSessioneVieneRifiutata() {
        Sessione sessione = sessioneGruppo(ADESSO.plusDays(1), 10);
        preparaAtletaESessione(sessione);
        Prenotazione giaPresente = new Prenotazione(rossi, sessione);
        when(prenotazioneRepository.findByAtletaIdAndSessioneId(1L, 2L)).thenReturn(Optional.of(giaPresente));

        assertThrows(RegolaViolataException.class, () -> service.prenota(1L, 2L));
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void prenotaSessionePienaVieneRifiutata() {
        Sessione sessione = sessioneGruppo(ADESSO.plusDays(1), 10);
        preparaAtletaESessione(sessione);
        when(prenotazioneRepository.findByAtletaIdAndSessioneId(1L, 2L)).thenReturn(Optional.empty());
        when(prenotazioneRepository.countBySessioneIdAndStatoNot(2L, StatoPrenotazione.ANNULLATA)).thenReturn(10L);

        assertThrows(RegolaViolataException.class, () -> service.prenota(1L, 2L));
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void prenotaDopoAnnullamentoRiattivaLaStessaPrenotazione() {
        Sessione sessione = sessioneGruppo(ADESSO.plusDays(1), 10);
        preparaAtletaESessione(sessione);
        Prenotazione annullata = new Prenotazione(rossi, sessione);
        annullata.annulla();
        when(prenotazioneRepository.findByAtletaIdAndSessioneId(1L, 2L)).thenReturn(Optional.of(annullata));
        when(prenotazioneRepository.countBySessioneIdAndStatoNot(2L, StatoPrenotazione.ANNULLATA)).thenReturn(3L);
        restituisciCioCheVieneSalvato();

        Prenotazione risultato = service.prenota(1L, 2L);

        assertSame(annullata, risultato);
        assertEquals(StatoPrenotazione.PRENOTATA, risultato.getStato());
    }

    // ---------- annulla ----------

    @Test
    void annullaPrimaDellInizioFunziona() {
        Prenotazione prenotazione = new Prenotazione(rossi, sessioneGruppo(ADESSO.plusDays(1), 10));
        when(prenotazioneRepository.findById(5L)).thenReturn(Optional.of(prenotazione));
        restituisciCioCheVieneSalvato();

        Prenotazione risultato = service.annulla(5L);

        assertEquals(StatoPrenotazione.ANNULLATA, risultato.getStato());
    }

    @Test
    void annullaDopoLInizioVieneRifiutato() {
        Prenotazione prenotazione = new Prenotazione(rossi, sessioneGruppo(ADESSO.minusHours(1), 10));
        when(prenotazioneRepository.findById(5L)).thenReturn(Optional.of(prenotazione));

        assertThrows(RegolaViolataException.class, () -> service.annulla(5L));
    }

    @Test
    void annullaDueVolteVieneRifiutato() {
        Prenotazione prenotazione = new Prenotazione(rossi, sessioneGruppo(ADESSO.plusDays(1), 10));
        prenotazione.annulla();
        when(prenotazioneRepository.findById(5L)).thenReturn(Optional.of(prenotazione));

        assertThrows(RegolaViolataException.class, () -> service.annulla(5L));
    }

    // ---------- presenze ----------

    @Test
    void presenzaSuGruppoPrimaDellInizioVieneRifiutata() {
        Prenotazione prenotazione = new Prenotazione(rossi, sessioneGruppo(ADESSO.plusHours(1), 10));
        when(prenotazioneRepository.findById(5L)).thenReturn(Optional.of(prenotazione));

        assertThrows(RegolaViolataException.class, () -> service.segnaPresente(5L));
    }

    @Test
    void presenzaSuGruppoDopoLInizioFunziona() {
        Prenotazione prenotazione = new Prenotazione(rossi, sessioneGruppo(ADESSO.minusHours(1), 10));
        when(prenotazioneRepository.findById(5L)).thenReturn(Optional.of(prenotazione));
        restituisciCioCheVieneSalvato();

        Prenotazione risultato = service.segnaPresente(5L);

        assertEquals(StatoPrenotazione.PRESENTE, risultato.getStato());
    }

    @Test
    void assenzaSuIndividualeSiPuoSegnarePrimaDellInizio() {
        Prenotazione prenotazione = new Prenotazione(rossi, sessioneIndividuale(ADESSO.plusDays(1)));
        when(prenotazioneRepository.findById(5L)).thenReturn(Optional.of(prenotazione));
        restituisciCioCheVieneSalvato();

        Prenotazione risultato = service.segnaAssente(5L);

        assertEquals(StatoPrenotazione.ASSENTE, risultato.getStato());
    }

    @Test
    void presenzaSuPrenotazioneAnnullataVieneRifiutata() {
        Prenotazione prenotazione = new Prenotazione(rossi, sessioneIndividuale(ADESSO.plusDays(1)));
        prenotazione.annulla();
        when(prenotazioneRepository.findById(5L)).thenReturn(Optional.of(prenotazione));

        assertThrows(RegolaViolataException.class, () -> service.segnaPresente(5L));
    }

    // ---------- metodi di supporto ----------

    private Sessione sessioneGruppo(LocalDateTime inizio, int capienza) {
        return new Sessione(inizio.toLocalDate(), inizio.toLocalTime(),
                TipoSessione.GRUPPO, Categoria.GIOVANILE, capienza, null);
    }

    private Sessione sessioneIndividuale(LocalDateTime inizio) {
        return new Sessione(inizio.toLocalDate(), inizio.toLocalTime(),
                TipoSessione.INDIVIDUALE, null, 1, null);
    }

    private void preparaAtletaESessione(Sessione sessione) {
        when(atletaRepository.findById(1L)).thenReturn(Optional.of(rossi));
        when(sessioneRepository.findById(2L)).thenReturn(Optional.of(sessione));
    }

    private void restituisciCioCheVieneSalvato() {
        when(prenotazioneRepository.save(any(Prenotazione.class)))
                .thenAnswer(invocazione -> invocazione.getArgument(0));
    }
}
