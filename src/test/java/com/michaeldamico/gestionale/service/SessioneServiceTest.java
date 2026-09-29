package com.michaeldamico.gestionale.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.michaeldamico.gestionale.dto.SessioneRequest;
import com.michaeldamico.gestionale.entity.Allenatore;
import com.michaeldamico.gestionale.entity.Categoria;
import com.michaeldamico.gestionale.entity.Sessione;
import com.michaeldamico.gestionale.entity.TipoSessione;
import com.michaeldamico.gestionale.exception.RegolaViolataException;
import com.michaeldamico.gestionale.exception.RisorsaNonTrovataException;
import com.michaeldamico.gestionale.repository.AllenatoreRepository;
import com.michaeldamico.gestionale.repository.SessioneRepository;

@ExtendWith(MockitoExtension.class)
class SessioneServiceTest {

    private static final LocalDate DATA = LocalDate.of(2026, 10, 2);
    private static final LocalTime ORA = LocalTime.of(18, 30);

    @Mock
    private SessioneRepository sessioneRepository;
    @Mock
    private AllenatoreRepository allenatoreRepository;

    private SessioneService service;

    @BeforeEach
    void preparaService() {
        service = new SessioneService(sessioneRepository, allenatoreRepository);
    }

    @Test
    void sessioneIndividualeHaSempreCapienzaUnoESenzaCategoria() {
        SessioneRequest request = new SessioneRequest(DATA, ORA, TipoSessione.INDIVIDUALE,
                Categoria.AGONISTI, 5, null);
        restituisciCioCheVieneSalvato();

        Sessione risultato = service.crea(request);

        assertEquals(1, risultato.getCapienza());
        assertNull(risultato.getCategoria());
    }

    @Test
    void sessioneDiGruppoValidaVieneSalvataComeRichiesta() {
        SessioneRequest request = new SessioneRequest(DATA, ORA, TipoSessione.GRUPPO,
                Categoria.GIOVANILE, 12, null);
        restituisciCioCheVieneSalvato();

        Sessione risultato = service.crea(request);

        assertEquals(12, risultato.getCapienza());
        assertEquals(Categoria.GIOVANILE, risultato.getCategoria());
        assertEquals(DATA, risultato.getData());
    }

    @Test
    void sessioneDiGruppoSenzaCategoriaVieneRifiutata() {
        SessioneRequest request = new SessioneRequest(DATA, ORA, TipoSessione.GRUPPO,
                null, 12, null);

        assertThrows(RegolaViolataException.class, () -> service.crea(request));
        verify(sessioneRepository, never()).save(any());
    }

    @Test
    void sessioneDiGruppoConCapienzaZeroVieneRifiutata() {
        SessioneRequest request = new SessioneRequest(DATA, ORA, TipoSessione.GRUPPO,
                Categoria.GIOVANILE, 0, null);

        assertThrows(RegolaViolataException.class, () -> service.crea(request));
        verify(sessioneRepository, never()).save(any());
    }

    @Test
    void sessioneDiGruppoSenzaCapienzaVieneRifiutata() {
        SessioneRequest request = new SessioneRequest(DATA, ORA, TipoSessione.GRUPPO,
                Categoria.GIOVANILE, null, null);

        assertThrows(RegolaViolataException.class, () -> service.crea(request));
        verify(sessioneRepository, never()).save(any());
    }

    @Test
    void allenatoreInesistenteLanciaRisorsaNonTrovata() {
        SessioneRequest request = new SessioneRequest(DATA, ORA, TipoSessione.INDIVIDUALE,
                null, null, 99L);
        when(allenatoreRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RisorsaNonTrovataException.class, () -> service.crea(request));
        verify(sessioneRepository, never()).save(any());
    }

    @Test
    void allenatoreEsistenteVieneCollegatoAllaSessione() {
        Allenatore allenatore = new Allenatore("Giulia", "Neri");
        SessioneRequest request = new SessioneRequest(DATA, ORA, TipoSessione.INDIVIDUALE,
                null, null, 7L);
        when(allenatoreRepository.findById(7L)).thenReturn(Optional.of(allenatore));
        restituisciCioCheVieneSalvato();

        Sessione risultato = service.crea(request);

        assertSame(allenatore, risultato.getAllenatore());
    }

    private void restituisciCioCheVieneSalvato() {
        when(sessioneRepository.save(any(Sessione.class)))
                .thenAnswer(invocazione -> invocazione.getArgument(0));
    }
}
